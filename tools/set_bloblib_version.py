#!/usr/bin/env python3
"""Set the BlobLib dependency version across every maintained repo.

Which pom holds the dependency differs per repo, so the script does not guess by
layout — it scans every pom.xml in the repo (skipping target/) and rewrites only the
ones that actually declare io.github.anjoismysign:bloblib. As of this writing that is
the root pom for most repos, but the `-paper` module for BlobOutlaw and
profile-permissions, and the root pom (not a module) for BlobProperties.

If the dependency's version is a property reference (${bloblib.version}), the property
definition is updated instead of the dependency block.

Before writing, each repo gets a new branch named after the version (e.g. `1.701`),
created from its base branch. An existing branch of that name is reused only when it
already contains the base; a stale one (cut before the last sync) is refused rather than
built on, unless --rebase-existing moves it forward. The base is the repo's own default
branch, taken from the remote's HEAD — `master` for some repos, `main` for others — and
can be forced with --base.
A repo with uncommitted changes is skipped rather than dragged onto the new branch;
pass --allow-dirty to branch anyway (the changes come along, as with any checkout).

Usage:
    python3 tools/set_bloblib_version.py 1.701 [-l LIST] [--only NAME ...] [--dry-run]
                                               [--base BRANCH] [--allow-dirty]
                                               [--no-branch]
"""

from __future__ import annotations

import argparse
import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

GROUP_ID = "io.github.anjoismysign"
ARTIFACT_ID = "bloblib"
MAVEN_NS = "{http://maven.apache.org/POM/4.0.0}"

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"

# One <dependency> block declaring bloblib, capturing its <version> text.
DEP_RE = re.compile(
    r"<dependency>(?:(?!</dependency>).)*?"
    rf"<artifactId>\s*{ARTIFACT_ID}\s*</artifactId>"
    r"(?:(?!</dependency>).)*?</dependency>",
    re.DOTALL,
)
VERSION_RE = re.compile(r"(<version>)(\s*)(.*?)(\s*)(</version>)", re.DOTALL)
PROPERTY_REF_RE = re.compile(r"^\$\{(.+)\}$")


def parse_list(path: Path) -> list[Path]:
    repos: list[Path] = []
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        if line.startswith(("- ", "* ", "+ ")):
            line = line[2:].strip()
        line = line.strip("`").strip()
        if line:
            repos.append(Path(os.path.expanduser(line)))
    return repos


def git(repo: Path, *cmd: str) -> subprocess.CompletedProcess:
    return subprocess.run(["git", "-C", str(repo), *cmd],
                          capture_output=True, text=True)


def is_repo(repo: Path) -> bool:
    return git(repo, "rev-parse", "--git-dir").returncode == 0


def has_ref(repo: Path, ref: str) -> bool:
    return git(repo, "rev-parse", "--verify", "--quiet", ref).returncode == 0


def current_branch(repo: Path) -> str:
    return git(repo, "rev-parse", "--abbrev-ref", "HEAD").stdout.strip()


def is_dirty(repo: Path) -> bool:
    """Tracked modifications only — untracked files ride along a checkout harmlessly."""
    return bool(git(repo, "status", "--porcelain", "--untracked-files=no").stdout.strip())


def last_line(proc: subprocess.CompletedProcess) -> str:
    lines = (proc.stderr or proc.stdout).strip().splitlines()
    return lines[-1].strip() if lines else f"exit {proc.returncode}"


def base_branch(repo: Path, requested: str | None) -> str | None:
    """The branch to base the new branch on: the repo's own default branch."""
    if requested:
        return requested if has_ref(repo, requested) else None
    # Ask the remote which branch is the default, so a repo on `main` and a repo on
    # `master` each get the right base even when both refs exist locally.
    head = git(repo, "symbolic-ref", "--short", "refs/remotes/origin/HEAD").stdout.strip()
    if head.startswith("origin/"):
        candidate = head[len("origin/"):]
        if has_ref(repo, candidate):
            return candidate
    for candidate in ("master", "main"):
        if has_ref(repo, candidate):
            return candidate
    return None


def contains(repo: Path, branch: str, base: str) -> bool:
    """True when `branch` already holds every commit on `base`."""
    return git(repo, "merge-base", "--is-ancestor", base, branch).returncode == 0


def make_branch(repo: Path, version: str, args: argparse.Namespace) -> tuple[bool, str]:
    """Create/switch to the version branch. Returns (ok, message)."""
    if not is_repo(repo):
        return False, "not a git repository"
    if is_dirty(repo) and not args.allow_dirty:
        return False, "uncommitted tracked changes — commit/stash them, or pass --allow-dirty"

    base = base_branch(repo, args.base)
    if base is None:
        wanted = args.base or "master/main"
        return False, f"no base branch {wanted} in this repo"

    on_it = current_branch(repo) == version

    if has_ref(repo, version):
        # An existing version branch cut before the last sync holds stale code, and
        # building on it silently compiles against the old sources. Refuse it unless
        # it already contains the base, or --rebase-existing is passed to move it.
        if not contains(repo, version, base):
            if not args.rebase_existing:
                return False, (f"branch {version} is stale — missing commits from {base};"
                               " rerun with --rebase-existing, or delete it")
            if args.dry_run:
                return True, f"would rebase existing branch {version} onto {base}"
            if not on_it:
                proc = git(repo, "checkout", version)
                if proc.returncode != 0:
                    return False, f"checkout {version} failed: {last_line(proc)}"
            proc = git(repo, "rebase", base)
            if proc.returncode != 0:
                git(repo, "rebase", "--abort")
                return False, f"rebase {version} onto {base} failed: {last_line(proc)}"
            return True, f"rebased existing branch {version} onto {base}"

        if on_it:
            return True, f"already on branch {version}"
        if args.dry_run:
            return True, f"would switch to existing branch {version}"
        proc = git(repo, "checkout", version)
        if proc.returncode != 0:
            return False, f"checkout {version} failed: {last_line(proc)}"
        return True, f"switched to existing branch {version}"

    if on_it:
        return True, f"already on branch {version}"

    if args.dry_run:
        return True, f"would create branch {version} from {base}"

    proc = git(repo, "checkout", "-b", version, base)
    if proc.returncode != 0:
        tail = proc.stderr.strip().splitlines()
        return False, f"branch {version} from {base} failed: {tail[-1] if tail else '?'}"
    return True, f"created branch {version} from {base}"


def poms(repo: Path) -> list[Path]:
    return sorted(p for p in repo.rglob("pom.xml") if "target" not in p.parts)


def declares_bloblib(pom: Path) -> bool:
    """True when this pom has a bloblib dependency with the expected groupId."""
    try:
        root = ET.parse(pom).getroot()
    except ET.ParseError:
        return False
    for dep in root.iter():
        tag = dep.tag.replace(MAVEN_NS, "")
        if tag != "dependency":
            continue
        got = {}
        for child in dep:
            got[child.tag.replace(MAVEN_NS, "")] = (child.text or "").strip()
        if got.get("groupId") == GROUP_ID and got.get("artifactId") == ARTIFACT_ID:
            return True
    return False


def set_property(text: str, name: str, version: str) -> tuple[str, str | None]:
    """Rewrite <name>…</name> inside <properties>. Returns (text, old_value)."""
    prop_re = re.compile(rf"(<{re.escape(name)}>)(\s*)(.*?)(\s*)(</{re.escape(name)}>)",
                         re.DOTALL)
    match = prop_re.search(text)
    if match is None:
        return text, None
    old = match.group(3)
    if old == version:
        return text, old
    start, end = match.span()
    replacement = f"{match.group(1)}{match.group(2)}{version}{match.group(4)}{match.group(5)}"
    return text[:start] + replacement + text[end:], old


def update_pom(pom: Path, version: str, dry_run: bool) -> str:
    """Rewrite the bloblib version in `pom`. Returns a human-readable outcome."""
    text = pom.read_text(encoding="utf-8")

    blocks = DEP_RE.findall(text)
    if not blocks:
        return "no bloblib <dependency> block found (unexpected)"
    if len(blocks) > 1:
        return f"{len(blocks)} bloblib dependency blocks found — left untouched, fix by hand"

    block = blocks[0]
    vmatch = VERSION_RE.search(block)
    if vmatch is None:
        return "dependency has no <version> (inherited from dependencyManagement?) — skipped"

    old = vmatch.group(3).strip()
    prop = PROPERTY_REF_RE.match(old)

    if prop:
        name = prop.group(1)
        new_text, prop_old = set_property(text, name, version)
        if prop_old is None:
            return f"version is ${{{name}}} but that property is not defined here — skipped"
        if prop_old == version:
            return f"already {version} (property {name})"
        outcome = f"{prop_old} -> {version} (property {name})"
    else:
        if old == version:
            return f"already {version}"
        new_block = (
            block[:vmatch.start()]
            + f"{vmatch.group(1)}{vmatch.group(2)}{version}{vmatch.group(4)}{vmatch.group(5)}"
            + block[vmatch.end():]
        )
        new_text = text.replace(block, new_block, 1)
        outcome = f"{old} -> {version}"

    if not dry_run:
        pom.write_text(new_text, encoding="utf-8")
    return outcome


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("version", help="BlobLib version to set, e.g. 1.701")
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="limit to repos whose directory name matches (case-insensitive)")
    parser.add_argument("--dry-run", action="store_true",
                        help="report what would change without writing or touching git")
    parser.add_argument("--base", default=None, metavar="BRANCH",
                        help="base branch for the new branch"
                             " (default: the repo's own default branch)")
    parser.add_argument("--rebase-existing", action="store_true",
                        help="rebase an existing version branch onto the base when it is"
                             " missing commits, instead of refusing it as stale")
    parser.add_argument("--allow-dirty", action="store_true",
                        help="branch even when the repo has uncommitted changes")
    parser.add_argument("--no-branch", action="store_true",
                        help="edit poms in place on the current branch, creating no branch")
    args = parser.parse_args()

    if not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    repos = parse_list(args.list)
    if args.only:
        wanted = {n.lower() for n in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("nothing to do", file=sys.stderr)
        return 2

    problems = 0
    for repo in repos:
        if not repo.is_dir():
            print(f"SKIP  {repo.name}: directory not found")
            problems += 1
            continue

        targets = [p for p in poms(repo) if declares_bloblib(p)]
        if not targets:
            print(f"SKIP  {repo.name}: no pom declares {GROUP_ID}:{ARTIFACT_ID}")
            problems += 1
            continue

        if not args.no_branch:
            ok, message = make_branch(repo, args.version, args)
            print(f"{'GIT   ' if ok else 'SKIP  '}{repo.name}: {message}")
            if not ok:
                problems += 1
                continue  # never edit poms on a branch we did not establish

        for pom in targets:
            rel = pom.relative_to(repo)
            outcome = update_pom(pom, args.version, args.dry_run)
            prefix = "DRY   " if args.dry_run else "SET   "
            if "skipped" in outcome or "unexpected" in outcome or "by hand" in outcome:
                prefix = "WARN  "
                problems += 1
            print(f"{prefix}{repo.name}/{rel}: {outcome}")

    if problems:
        print(f"\n{problems} repo(s)/pom(s) need attention", file=sys.stderr)
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
