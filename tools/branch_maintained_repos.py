#!/usr/bin/env python3
"""Print the branch each repo in bloblib-memory/maintained repos.md is checked out on.

Read-only: it runs nothing but `git rev-parse` and friends, touches no working tree and
talks to no remote (so it is instant, and honest about being a snapshot of what is on
disk rather than of what GitHub currently holds).

Alongside the current branch it shows the repo's default branch — read from the remote's
HEAD, `main` for some repos and `master` for others — and flags any repo sitting somewhere
else, which is exactly what deploy_maintained_repos.py refuses to publish from.

It also reports the state of the working tree, counting uncommitted changes to tracked
files separately from untracked files: the first is what a deploy would publish and a
push would carry, the second is usually noise (a .DS_Store, a scratch script) and is
what the deploy preflight deliberately ignores. Add --verbose for how far ahead or
behind upstream each branch is.

Exit status is 1 when at least one repo is off its default branch, so it can gate a
deploy in a shell chain; --quiet prints only those repos.

Usage:
    python3 tools/branch_maintained_repos.py [-l LIST] [--only NAME ...]
                                             [-v] [--quiet]
"""

from __future__ import annotations

import argparse
import os
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"


@dataclass
class Row:
    name: str
    branch: str
    base: str
    on_base: bool
    detail: str = ""


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
    return subprocess.run(["git", "-C", str(repo), *cmd], capture_output=True, text=True)


def out(repo: Path, *cmd: str) -> str:
    proc = git(repo, *cmd)
    return proc.stdout.strip() if proc.returncode == 0 else ""


def has_ref(repo: Path, ref: str) -> bool:
    return git(repo, "show-ref", "--verify", "--quiet", ref).returncode == 0


def remote_name(repo: Path) -> str | None:
    remotes = out(repo, "remote").splitlines()
    if not remotes:
        return None
    return "origin" if "origin" in remotes else remotes[0].strip()


def default_branch(repo: Path, remote: str) -> str | None:
    """Branch the remote's HEAD points at — `main` for some repos, `master` for others."""
    head = out(repo, "symbolic-ref", "--short", f"refs/remotes/{remote}/HEAD")
    if head.startswith(f"{remote}/"):
        return head[len(remote) + 1:]
    # No fetching here: this tool stays read-only and offline, so fall back to what the
    # local clone already knows rather than asking the remote.
    for guess in ("main", "master"):
        if has_ref(repo, f"refs/remotes/{remote}/{guess}"):
            return guess
    return None


def tracking(repo: Path, remote: str, branch: str) -> str:
    """`3 ahead`, `2 behind`, `in sync`, or why neither applies."""
    upstream = f"{remote}/{branch}"
    if not has_ref(repo, f"refs/remotes/{upstream}"):
        return "unpublished"
    counts = out(repo, "rev-list", "--left-right", "--count", f"HEAD...{upstream}")
    if not counts:
        return ""
    ahead, behind = (counts.split() + ["0", "0"])[:2]
    parts = []
    if ahead != "0":
        parts.append(f"{ahead} ahead")
    if behind != "0":
        parts.append(f"{behind} behind")
    return ", ".join(parts) if parts else "in sync"


def worktree(repo: Path) -> str:
    """`2 uncommitted, 5 untracked`, or "" for a clean tree.

    The two are counted apart on purpose: uncommitted changes to tracked files are what
    a deploy publishes and what a push would carry, while untracked files (a .DS_Store,
    a scratch script) are usually noise — which is why the deploy preflight ignores them.
    Untracked directories are counted as one entry each, as git reports them.
    """
    lines = out(repo, "status", "--porcelain").splitlines()
    untracked = sum(1 for line in lines if line.startswith("??"))
    uncommitted = len(lines) - untracked

    parts = []
    if uncommitted:
        parts.append(f"{uncommitted} uncommitted")
    if untracked:
        parts.append(f"{untracked} untracked")
    return ", ".join(parts)


def inspect(repo: Path, args: argparse.Namespace) -> Row:
    name = repo.name
    if not repo.is_dir():
        return Row(name, "-", "-", False, "directory not found")
    if git(repo, "rev-parse", "--git-dir").returncode != 0:
        return Row(name, "-", "-", False, "not a git repository")

    branch = out(repo, "rev-parse", "--abbrev-ref", "HEAD") or "?"
    if branch == "HEAD":
        branch = f"detached@{out(repo, 'rev-parse', '--short', 'HEAD')}"

    remote = remote_name(repo)
    base = (default_branch(repo, remote) or "?") if remote else "?"
    on_base = branch == base

    bits = []
    if args.verbose and remote and on_base:
        state = tracking(repo, remote, branch)
        if state:
            bits.append(state)
    state = worktree(repo)
    bits.append(state if state else "clean")
    return Row(name, branch, base, on_base, ", ".join(bits))


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="limit to repos whose directory name matches (case-insensitive)")
    parser.add_argument("-v", "--verbose", action="store_true",
                        help="also show how far ahead/behind upstream each branch is")
    parser.add_argument("--quiet", action="store_true",
                        help="list only repos that are not on their default branch")
    args = parser.parse_args()

    if not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    repos = parse_list(args.list)
    if args.only:
        wanted = {n.lower() for n in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("no repos to inspect", file=sys.stderr)
        return 2

    rows = [inspect(r, args) for r in repos]
    shown = [row for row in rows if not row.on_base] if args.quiet else rows

    if shown:
        name_w = max(len(r.name) for r in shown)
        branch_w = max(len(r.branch) for r in shown)
        for r in shown:
            mark = "  " if r.on_base else "* "
            base = "" if r.on_base else f"(default: {r.base})"
            trail = "  ".join(x for x in (base, r.detail) if x)
            print(f"{mark}{r.name:<{name_w}}  {r.branch:<{branch_w}}  {trail}".rstrip())

    off = [r for r in rows if not r.on_base]
    if off:
        print(f"\n{len(off)} of {len(rows)} not on their default branch "
              f"(marked *): {', '.join(r.name for r in off)}")
    elif not args.quiet:
        print(f"\nall {len(rows)} on their default branch")
    return 1 if off else 0


if __name__ == "__main__":
    sys.exit(main())
