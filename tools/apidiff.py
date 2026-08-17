#!/usr/bin/env python3
"""
apidiff — diff two versions of a JAR and migrate downstream sources.

Answers the question "what moved, what died, and which of it can I fix without
thinking?" by combining two deterministic signals:

  1. class inventory  (zip listing)      -> what FQNs appeared/disappeared
  2. public API       (javap)            -> whether a same-named class is
                                            actually the same class

Signal 2 is the important one. Matching on class name alone will happily map
  io.github...bloblib.events.ProfileLoadEvent
    -> net.milkbowl.vault.profile.ProfileLoadEvent
and never notice that getProfile() was dropped. Comparing member signatures
catches that and kicks it up a tier.

Findings are sorted into three tiers:

  AUTO    unambiguous relocation, public API unchanged.
          -> safe to rewrite mechanically. `apply` does exactly these.
  REVIEW  needs judgment a script should not fake: API changed, or several
          equally plausible targets, or gone-but-something-similar-exists.
          -> hand to an LLM, or read it yourself.
  MANUAL  gone, no candidate at all. A design decision.
          -> human.

Usage
  apidiff.py map  OLD.jar NEW.jar [-o map.json] [--prefix io.github.foo]
  apidiff.py scan map.json PROJECT_DIR [PROJECT_DIR ...]
  apidiff.py apply map.json PROJECT_DIR [--dry-run]

`map` and `scan` only read. `apply` edits .java files in place and touches only
AUTO entries; anything else it reports and leaves alone.

No third-party dependencies. Needs `javap` on PATH (any JDK).
"""

from __future__ import annotations

import argparse
import difflib
import json
import os
import re
import subprocess
import sys
import zipfile
from collections import defaultdict
from concurrent.futures import ThreadPoolExecutor

AUTO, REVIEW, MANUAL = "AUTO", "REVIEW", "MANUAL"


# ---------------------------------------------------------------- inventory

def list_classes(jar: str) -> list[str]:
    """Public top-level classes in a jar, as dotted FQNs."""
    out = []
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if not n.endswith(".class"):
                continue
            if "$" in n:                       # inner/anonymous: follow the outer class
                continue
            if n.endswith("package-info.class") or n.endswith("module-info.class"):
                continue
            out.append(n[:-len(".class")].replace("/", "."))
    return sorted(out)


# ------------------------------------------------------------------- javap

_DROP = re.compile(r"\b(public|protected|private|static|final|abstract|native|"
                   r"synchronized|transient|volatile|default|strictfp)\b")
_GENERIC = re.compile(r"<[^<>]*>")


def _normalize(sig: str) -> str:
    """
    Reduce a javap member line to something comparable across versions.

    Modifiers and generics are dropped: a method going public->protected, or
    List<String>->List<T>, is not the kind of break this tool is hunting for,
    and keeping them produces noise on every single class.
    """
    s = sig.strip().rstrip(";")
    s = _DROP.sub("", s)
    prev = None
    while prev != s:                            # nested generics
        prev = s
        s = _GENERIC.sub("", s)
    s = re.sub(r"\bthrows\b.*$", "", s)
    s = re.sub(r"\s+", " ", s).strip()
    # collapse fully-qualified types to simple names so a moved *parameter*
    # type does not by itself count as an API change
    s = re.sub(r"\b(?:[a-z_][A-Za-z0-9_]*\.)+([A-Z][A-Za-z0-9_]*)", r"\1", s)
    return s


def api_of(jar: str, classes: list[str], workers: int = 8) -> dict[str, set[str]]:
    """{fqn: {normalized member signature}} via javap, batched and threaded."""
    if not classes:
        return {}
    batches = [classes[i:i + 60] for i in range(0, len(classes), 60)]

    def run(batch: list[str]) -> str:
        try:
            p = subprocess.run(["javap", "-cp", jar, *batch],
                               capture_output=True, text=True, timeout=300)
            return p.stdout
        except (subprocess.SubprocessError, OSError):
            return ""

    with ThreadPoolExecutor(max_workers=workers) as ex:
        chunks = list(ex.map(run, batches))

    api: dict[str, set[str]] = {}
    current = None
    header = re.compile(r"^(?:[\w\s]*?)\b(?:class|interface|enum|record)\s+"
                        r"([A-Za-z0-9_.$]+)")
    for text in chunks:
        for line in text.splitlines():
            if not line.strip() or line.startswith("Compiled from"):
                continue
            if not line.startswith(" "):                 # type declaration
                m = header.search(line)
                current = m.group(1).split("<")[0] if m else None
                if current:
                    api.setdefault(current, set())
                continue
            if current is None or line.strip() in ("}", "{"):
                continue
            if "static {}" in line:
                continue
            api[current].add(_normalize(line))
    return api


# ------------------------------------------------------------------ pairing

def rank(old_fqn: str, candidates: list[str], prefix: str | None) -> list[str]:
    """
    Order candidate targets for a moved class, best first.

    Prefers, in order: sharing the library's own package prefix, then the
    longest shared package path, then raw string similarity of the package.
    This is what resolves `managers.Manager` to `bloblib.manager.Manager`
    rather than a same-named class from a bundled third-party dependency.
    """
    old_pkg = old_fqn.rsplit(".", 1)[0]

    def score(c: str):
        pkg = c.rsplit(".", 1)[0]
        in_prefix = bool(prefix and c.startswith(prefix))
        a, b = old_pkg.split("."), pkg.split(".")
        shared = 0
        for x, y in zip(a, b):
            if x != y:
                break
            shared += 1
        return (in_prefix, shared, difflib.SequenceMatcher(None, old_pkg, pkg).ratio())

    return sorted(candidates, key=score, reverse=True)


def build_map(old_jar: str, new_jar: str, prefix: str | None) -> dict:
    old_cls, new_cls = list_classes(old_jar), list_classes(new_jar)
    old_set, new_set = set(old_cls), set(new_cls)
    vanished = sorted(old_set - new_set)

    by_simple = defaultdict(list)
    for c in new_cls:
        by_simple[c.rsplit(".", 1)[1]].append(c)

    # only javap what we actually need to compare
    need_old = [c for c in vanished if c.rsplit(".", 1)[1] in by_simple]
    need_new = sorted({t for c in need_old for t in by_simple[c.rsplit(".", 1)[1]]})
    sys.stderr.write(f"[apidiff] javap: {len(need_old)} old + {len(need_new)} new classes\n")
    old_api = api_of(old_jar, need_old)
    new_api = api_of(new_jar, need_new)

    entries = []
    for old in vanished:
        simple = old.rsplit(".", 1)[1]
        cands = by_simple.get(simple, [])
        if not cands:
            entries.append({"old": old, "tier": MANUAL, "target": None,
                            "candidates": [], "reason": "no class with this name in new jar"})
            continue

        ordered = rank(old, cands, prefix)
        target = ordered[0]
        before, after = old_api.get(old, set()), new_api.get(target, set())

        if len(ordered) > 1:
            tier, reason = REVIEW, f"{len(ordered)} same-named candidates; picked by package similarity"
        elif not before or not after:
            tier, reason = REVIEW, "could not read API of one side (javap produced nothing)"
        elif before == after:
            tier, reason = AUTO, "relocation, public API identical"
        else:
            lost = sorted(before - after)
            tier = REVIEW
            reason = ("relocation, but API differs — lost: "
                      + ", ".join(lost[:4]) + ("…" if len(lost) > 4 else "")) if lost \
                     else "relocation, but API differs (members added only)"

        entries.append({"old": old, "tier": tier, "target": target,
                        "candidates": ordered, "reason": reason,
                        "lost": sorted(before - after), "gained": sorted(after - before)})

    return {"old_jar": os.path.basename(old_jar), "new_jar": os.path.basename(new_jar),
            "prefix": prefix,
            "stats": {"old_classes": len(old_cls), "new_classes": len(new_cls),
                      "vanished": len(vanished),
                      AUTO: sum(e["tier"] == AUTO for e in entries),
                      REVIEW: sum(e["tier"] == REVIEW for e in entries),
                      MANUAL: sum(e["tier"] == MANUAL for e in entries)},
            "entries": entries}


# ------------------------------------------------------------------ project

FQN = re.compile(r"\b(?:[a-z][A-Za-z0-9_]*\.){2,}[A-Z][A-Za-z0-9_]*\b")


def java_files(root: str) -> list[str]:
    out = []
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames
                       if d not in {"target", "build", ".git", "out", ".idea"}]
        out += [os.path.join(dirpath, f) for f in filenames if f.endswith(".java")]
    return out


def scan(mapping: dict, root: str) -> dict:
    index = {e["old"]: e for e in mapping["entries"]}
    hits: dict[str, list[tuple[str, int]]] = defaultdict(list)
    for path in java_files(root):
        try:
            with open(path, encoding="utf-8", errors="replace") as fh:
                for lineno, line in enumerate(fh, 1):
                    for ref in FQN.findall(line):
                        if ref in index:
                            hits[ref].append((os.path.relpath(path, root), lineno))
        except OSError:
            continue
    return {"root": root, "hits": hits, "index": index}


def report(res: dict) -> int:
    hits, index = res["hits"], res["index"]
    buckets = defaultdict(list)
    for ref in hits:
        buckets[index[ref]["tier"]].append(ref)

    name = os.path.basename(os.path.abspath(res["root"]))
    n_auto, n_rev, n_man = (len(buckets[t]) for t in (AUTO, REVIEW, MANUAL))
    print(f"\n=== {name}")
    print(f"    {len(hits)} affected classes referenced "
          f"— {n_auto} auto, {n_rev} review, {n_man} manual")

    for tier in (MANUAL, REVIEW, AUTO):
        if not buckets[tier]:
            continue
        print(f"\n  [{tier}]")
        for ref in sorted(buckets[tier]):
            e = index[ref]
            where = hits[ref]
            print(f"    {ref}")
            if e["target"] and tier != MANUAL:
                print(f"        -> {e['target']}")
            print(f"        {e['reason']}")
            if tier != AUTO:
                for f, ln in where[:6]:
                    print(f"        at {f}:{ln}")
                if len(where) > 6:
                    print(f"        … {len(where) - 6} more")
    return n_rev + n_man


def apply(mapping: dict, root: str, dry: bool) -> int:
    res = scan(mapping, root)
    hits, index = res["hits"], res["index"]
    auto = {r: index[r]["target"] for r in hits if index[r]["tier"] == AUTO}
    blocked = [r for r in hits if index[r]["tier"] != AUTO]

    if auto:
        # longest first so a.b.Foo is not clipped by a shorter overlapping key
        keys = sorted(auto, key=len, reverse=True)
        pat = re.compile("|".join(re.escape(k) for k in keys))
        changed = 0
        for path in java_files(root):
            with open(path, encoding="utf-8") as fh:
                src = fh.read()
            new = pat.sub(lambda m: auto[m.group(0)], src)
            if new != src:
                changed += 1
                if not dry:
                    with open(path, "w", encoding="utf-8") as fh:
                        fh.write(new)
        verb = "would rewrite" if dry else "rewrote"
        print(f"{verb} {changed} file(s) using {len(auto)} AUTO mapping(s) in {root}")
    else:
        print(f"no AUTO mappings apply to {root}")

    if blocked:
        print(f"\nleft alone — {len(blocked)} reference(s) need review:")
        for r in sorted(blocked):
            print(f"  [{index[r]['tier']}] {r}: {index[r]['reason']}")
    return len(blocked)


# --------------------------------------------------------------------- cli

def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = ap.add_subparsers(dest="cmd", required=True)

    m = sub.add_parser("map", help="diff two jars into a migration map")
    m.add_argument("old_jar"); m.add_argument("new_jar")
    m.add_argument("-o", "--out", default="migration-map.json")
    m.add_argument("--prefix", help="your library's package root, e.g. io.github.foo "
                                    "— disambiguates against bundled third-party classes")

    s = sub.add_parser("scan", help="report how a project is affected")
    s.add_argument("map"); s.add_argument("projects", nargs="+")

    a = sub.add_parser("apply", help="rewrite AUTO relocations in place")
    a.add_argument("map"); a.add_argument("projects", nargs="+")
    a.add_argument("--dry-run", action="store_true")

    args = ap.parse_args()

    if args.cmd == "map":
        mp = build_map(args.old_jar, args.new_jar, args.prefix)
        with open(args.out, "w") as fh:
            json.dump(mp, fh, indent=2)
        st = mp["stats"]
        print(f"{st['old_classes']} -> {st['new_classes']} classes, "
              f"{st['vanished']} vanished from their old FQN")
        print(f"  AUTO   {st[AUTO]:4d}  safe mechanical rewrite")
        print(f"  REVIEW {st[REVIEW]:4d}  needs an LLM or a careful read")
        print(f"  MANUAL {st[MANUAL]:4d}  needs a human decision")
        print(f"written to {args.out}")
        return 0

    with open(args.map) as fh:
        mp = json.load(fh)

    if args.cmd == "scan":
        return 1 if sum(report(scan(mp, p)) for p in args.projects) else 0
    return 1 if sum(apply(mp, p, args.dry_run) for p in args.projects) else 0


if __name__ == "__main__":
    sys.exit(main())
