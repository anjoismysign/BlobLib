#!/usr/bin/env python3
"""Print the version each project declares — BlobLib plus every repo in the maintained list.

The version is read from whichever build system the project uses: the root pom's
<version> (falling back to the parent's when inherited) for the Maven plugins, or
`version = "..."` in build.gradle.kts / gradle.properties for BlobLib. It is the same
lookup github_release_maintained_repos.py tags releases from, imported rather than
reimplemented so the two can never disagree about what version a repo is at.

Read-only and offline by default. Pass --released to also ask GitHub for each repo's
latest published release, which is what tells you whether a version has actually shipped:

    BlobEconomy   1.0.16   released v1.0.16   up to date
    blobmenu      1.1      released v1.1      up to date
    BlobRP        0.7.0    released v0.6.01   unreleased

Requires the `gh` CLI, authenticated, for --released only.

Usage:
    python3 tools/version_maintained_repos.py [-l LIST] [--only NAME ...]
                                              [--released] [--no-bloblib]
"""

from __future__ import annotations

import argparse
import os
import shutil
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import github_release_maintained_repos as shared  # noqa: E402

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"
BLOBLIB = Path(__file__).resolve().parent.parent


@dataclass
class Row:
    name: str
    version: str
    build: str
    released: str = ""
    state: str = ""


def build_system(repo: Path) -> str:
    if (repo / "pom.xml").is_file():
        return "maven"
    if (repo / "build.gradle.kts").is_file() or (repo / "build.gradle").is_file():
        return "gradle"
    return "-"


def latest_release(repo: Path) -> str:
    proc = subprocess.run(["gh", "release", "list", "--limit", "1",
                           "--json", "tagName", "--jq", ".[0].tagName"],
                          cwd=repo, capture_output=True, text=True)
    if proc.returncode != 0:
        return "?"
    return proc.stdout.strip() or "none"


def inspect(repo: Path, args: argparse.Namespace) -> Row:
    name = repo.name
    if not repo.is_dir():
        return Row(name, "-", "-", state="directory not found")

    build = build_system(repo)
    version = shared.project_version(repo) or "?"
    row = Row(name, version, build)
    if not args.released:
        return row

    row.released = latest_release(repo)
    if row.released in ("none", "?"):
        row.state = "never released" if row.released == "none" else "cannot read releases"
    elif row.released.lstrip("v") == version:
        row.state = "up to date"
    else:
        row.state = "unreleased"
    return row


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="limit to repos whose directory name matches (case-insensitive)")
    parser.add_argument("--released", action="store_true",
                        help="also show each repo's latest published release (needs gh)")
    parser.add_argument("--no-bloblib", action="store_true",
                        help="list only the maintained repos, leaving BlobLib out")
    args = parser.parse_args()

    if args.released and shutil.which("gh") is None:
        print("gh not on PATH — install it, or drop --released", file=sys.stderr)
        return 2
    if not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    repos = shared.parse_list(args.list)
    if not args.no_bloblib:
        repos.insert(0, BLOBLIB)
    if args.only:
        wanted = {n.lower() for n in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("no projects to inspect", file=sys.stderr)
        return 2

    rows = [inspect(r, args) for r in repos]
    name_w = max(len(r.name) for r in rows)
    ver_w = max(len(r.version) for r in rows)
    rel_w = max((len(r.released) for r in rows), default=0)

    for r in rows:
        line = f"{r.name:<{name_w}}  {r.version:<{ver_w}}  {r.build:<6}"
        if args.released:
            line += f"  {r.released:<{rel_w}}  {r.state}"
        elif r.state:
            line += f"  {r.state}"
        print(line.rstrip())

    unknown = [r.name for r in rows if r.version == "?"]
    if unknown:
        print(f"\nno version found for: {', '.join(unknown)}", file=sys.stderr)
    if args.released:
        behind = [r.name for r in rows if r.state in ("unreleased", "never released")]
        if behind:
            print(f"\n{len(behind)} with an unreleased version: {', '.join(behind)}")
    return 1 if unknown else 0


if __name__ == "__main__":
    sys.exit(main())
