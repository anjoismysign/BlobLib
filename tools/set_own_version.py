#!/usr/bin/env python3
"""Bump each maintained repo's own version to <bloblib-version>.<build>.

Every maintained repo tracks the BlobLib version it was built against as its own
major.minor, keeping a repo-local incremental build number as the patch: BlobProperties
at BlobLib 1.701, third build, is "1.701.2". The build number is per (repo, BlobLib
version) pair -- it resets to 0 the first time a repo moves onto a new BlobLib version,
and otherwise increments by one each time this script runs against a repo already on
that BlobLib version, so it never needs trailing zeroes across releases.

The version is read the same way version_maintained_repos.py reads it (imported, not
reimplemented), and written to every pom.xml in the repo: the root pom's own <version>,
and every submodule's <parent><version> that points at the root artifact -- Maven has no
${revision} property here, so the literal has to be updated everywhere it is inherited.

Before writing, each repo gets a new branch named after the new version (e.g. `1.701.2`),
created from its base branch, reusing the exact branching rules set_bloblib_version.py
uses (stale-branch refusal, --rebase-existing, --allow-dirty, --no-branch). See that
script's docstring for the details.

Usage:
    python3 tools/set_own_version.py 1.701 [-l LIST] [--only NAME ...] [--dry-run]
                                           [--build N] [--base BRANCH] [--allow-dirty]
                                           [--rebase-existing] [--no-branch]
"""

from __future__ import annotations

import argparse
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import github_release_maintained_repos as shared  # noqa: E402
from set_bloblib_version import make_branch  # noqa: E402

MAVEN_NS = "{http://maven.apache.org/POM/4.0.0}"
DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"

VERSION_RE = re.compile(r"(<version>)(\s*)(.*?)(\s*)(</version>)", re.DOTALL)
PARENT_RE = re.compile(r"<parent>(?:(?!</parent>).)*?</parent>", re.DOTALL)


def child(parent, tag):
    if parent is None:
        return None
    el = parent.find(f"{MAVEN_NS}{tag}")
    return el if el is not None else parent.find(tag)


def root_identity(pom: Path) -> tuple[str, str] | None:
    """The root pom's own (artifactId, version), or None when unreadable."""
    try:
        root = ET.parse(pom).getroot()
    except ET.ParseError:
        return None
    artifact = child(root, "artifactId")
    version = child(root, "version")
    if artifact is None or version is None or not artifact.text or not version.text:
        return None
    return artifact.text.strip(), version.text.strip()


def next_build(current: str | None, bloblib_version: str, forced: int | None) -> int:
    """Build number for this run: forced, else +1 on the same BlobLib version, else 0."""
    if forced is not None:
        return forced
    if current is not None:
        match = re.fullmatch(rf"{re.escape(bloblib_version)}\.(\d+)", current)
        if match:
            return int(match.group(1)) + 1
    return 0


def replace_root_version(text: str, artifact_id: str, old: str, new: str) -> str | None:
    """Rewrite the root pom's own <version>, anchored to its <artifactId>."""
    anchor = re.compile(
        rf"(<artifactId>\s*{re.escape(artifact_id)}\s*</artifactId>\s*)<version>\s*"
        rf"{re.escape(old)}\s*</version>",
        re.DOTALL,
    )
    match = anchor.search(text)
    if match is None:
        return None
    return text[:match.start()] + match.group(1) + f"<version>{new}</version>" + text[match.end():]


def replace_parent_version(text: str, artifact_id: str, old: str, new: str) -> tuple[str, int]:
    """Rewrite <parent><version> blocks pointing at the root artifact. Returns (text, count)."""
    count = 0

    def rewrite_block(match: re.Match) -> str:
        nonlocal count
        block = match.group(0)
        if not re.search(rf"<artifactId>\s*{re.escape(artifact_id)}\s*</artifactId>", block):
            return block
        vmatch = VERSION_RE.search(block)
        if vmatch is None or vmatch.group(3).strip() != old:
            return block
        count += 1
        return (block[:vmatch.start()]
                + f"{vmatch.group(1)}{vmatch.group(2)}{new}{vmatch.group(4)}{vmatch.group(5)}"
                + block[vmatch.end():])

    new_text = PARENT_RE.sub(rewrite_block, text)
    return new_text, count


def poms(repo: Path) -> list[Path]:
    return sorted(p for p in repo.rglob("pom.xml") if "target" not in p.parts)


def update_repo(repo: Path, new_version: str, old_version: str,
                dry_run: bool) -> list[tuple[bool, str]]:
    """Write `new_version` into the root pom and every inheriting <parent>.

    Returns one (ok, message) per pom touched (or one failure entry).
    """
    root_pom = repo / "pom.xml"
    if not root_pom.is_file():
        return [(False, "no root pom.xml")]

    identity = root_identity(root_pom)
    if identity is None:
        return [(False, "root pom has no readable artifactId/version")]
    artifact_id, root_old = identity
    if root_old != old_version:
        return [(False, f"root pom is at {root_old}, expected {old_version} -- left untouched")]

    text = root_pom.read_text(encoding="utf-8")
    new_text = replace_root_version(text, artifact_id, old_version, new_version)
    if new_text is None:
        return [(False, "could not locate the root <artifactId>/<version> pair -- left untouched")]

    results = [(True, f"pom.xml: {old_version} -> {new_version}")]
    if not dry_run:
        root_pom.write_text(new_text, encoding="utf-8")

    for pom in poms(repo):
        if pom == root_pom:
            continue
        text = pom.read_text(encoding="utf-8")
        new_text, count = replace_parent_version(text, artifact_id, old_version, new_version)
        if count == 0:
            continue
        results.append((True, f"{pom.relative_to(repo)}: parent {old_version} -> {new_version}"))
        if dry_run:
            continue
        pom.write_text(new_text, encoding="utf-8")

    return results


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("bloblib_version", help="BlobLib version to track, e.g. 1.701")
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="limit to repos whose directory name matches (case-insensitive)")
    parser.add_argument("--build", type=int, default=None,
                        help="force the build number instead of auto-incrementing")
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

    repos = shared.parse_list(args.list)
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

        current = shared.project_version(repo)
        build = next_build(current, args.bloblib_version, args.build)
        new_version = f"{args.bloblib_version}.{build}"

        if current is None:
            print(f"SKIP  {repo.name}: could not read current version")
            problems += 1
            continue
        if current == new_version:
            print(f"SKIP  {repo.name}: already {new_version}")
            continue

        if not args.no_branch:
            ok, message = make_branch(repo, new_version, args)
            print(f"{'GIT   ' if ok else 'SKIP  '}{repo.name}: {message}")
            if not ok:
                problems += 1
                continue  # never edit poms on a branch we did not establish

        for ok, message in update_repo(repo, new_version, current, args.dry_run):
            if not ok:
                print(f"WARN  {repo.name}/{message}")
                problems += 1
                continue
            prefix = "DRY   " if args.dry_run else "SET   "
            print(f"{prefix}{repo.name}/{message}")

    if problems:
        print(f"\n{problems} repo(s) need attention", file=sys.stderr)
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
