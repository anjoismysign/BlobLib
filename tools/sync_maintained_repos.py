#!/usr/bin/env python3
"""Fetch and fast-forward every repo listed in docs/maintained repos.md.

Fetching is always safe, so it runs unconditionally (with --prune). Pulling is not, so
the pull is a strict fast-forward of the current branch and is skipped — never forced —
when it could lose or entangle work: a dirty tree, no upstream, or a branch that has
diverged from its upstream. Those cases are reported for you to resolve by hand.

Use --branch to pull a specific branch instead of whatever each repo has checked out;
the repo is switched to it first (and skipped if it has no such branch).

Usage:
    python3 tools/sync_maintained_repos.py [-l LIST] [--only NAME ...] [-j N]
                                           [--fetch-only] [--branch BRANCH]
                                           [--allow-dirty] [--dry-run]
"""

from __future__ import annotations

import argparse
import concurrent.futures
import os
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "docs" / "maintained repos.md"


@dataclass
class Result:
    name: str
    status: str  # updated | current | skipped | failed
    detail: str


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


def out(repo: Path, *cmd: str) -> str:
    return git(repo, *cmd).stdout.strip()


def last_error(proc: subprocess.CompletedProcess) -> str:
    lines = (proc.stderr or proc.stdout).strip().splitlines()
    return lines[-1].strip() if lines else f"exit {proc.returncode}"


def sync(repo: Path, args: argparse.Namespace) -> Result:
    name = repo.name

    if not repo.is_dir():
        return Result(name, "skipped", "directory not found")
    if git(repo, "rev-parse", "--git-dir").returncode != 0:
        return Result(name, "skipped", "not a git repository")
    if not out(repo, "remote"):
        return Result(name, "skipped", "no remote configured")

    if args.dry_run:
        branch = args.branch or out(repo, "rev-parse", "--abbrev-ref", "HEAD")
        action = "fetch" if args.fetch_only else f"fetch + fast-forward {branch}"
        return Result(name, "current", f"would {action}")

    fetched = git(repo, "fetch", "--all", "--prune", "--tags")
    if fetched.returncode != 0:
        return Result(name, "failed", f"fetch failed: {last_error(fetched)}")
    if args.fetch_only:
        return Result(name, "current", "fetched")

    # Untracked files (stray .DS_Store, an unversioned .gitignore) survive a
    # fast-forward untouched, so only tracked modifications count as dirty.
    dirty = bool(out(repo, "status", "--porcelain", "--untracked-files=no"))
    if dirty and not args.allow_dirty:
        return Result(name, "skipped", "fetched; pull skipped — uncommitted tracked changes")

    if args.branch:
        if git(repo, "rev-parse", "--verify", "--quiet", args.branch).returncode != 0:
            return Result(name, "skipped", f"fetched; no branch {args.branch}")
        if out(repo, "rev-parse", "--abbrev-ref", "HEAD") != args.branch:
            checkout = git(repo, "checkout", args.branch)
            if checkout.returncode != 0:
                return Result(name, "failed",
                              f"fetched; checkout {args.branch} failed: {last_error(checkout)}")

    branch = out(repo, "rev-parse", "--abbrev-ref", "HEAD")
    if branch == "HEAD":
        return Result(name, "skipped", "fetched; detached HEAD")

    upstream = out(repo, "rev-parse", "--abbrev-ref", "--symbolic-full-name", "@{upstream}")
    if not upstream:
        return Result(name, "skipped", f"fetched; {branch} has no upstream")

    counts = out(repo, "rev-list", "--left-right", "--count", f"{upstream}...HEAD")
    behind, ahead = (int(n) for n in counts.split()) if counts else (0, 0)

    if behind == 0:
        state = f"{branch} up to date" + (f", {ahead} ahead of {upstream}" if ahead else "")
        return Result(name, "current", f"fetched; {state}")
    if ahead:
        return Result(name, "skipped",
                      f"fetched; {branch} diverged ({ahead} ahead, {behind} behind {upstream})"
                      " — rebase or merge by hand")

    pulled = git(repo, "merge", "--ff-only", upstream)
    if pulled.returncode != 0:
        return Result(name, "failed", f"fast-forward failed: {last_error(pulled)}")
    return Result(name, "updated", f"{branch} fast-forwarded {behind} commit(s) from {upstream}")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="limit to repos whose directory name matches (case-insensitive)")
    parser.add_argument("-j", "--jobs", type=int, default=4,
                        help="how many repos to sync concurrently (default 4)")
    parser.add_argument("--fetch-only", action="store_true",
                        help="fetch and prune, never move any branch")
    parser.add_argument("--branch", default=None, metavar="BRANCH",
                        help="check this branch out before pulling, in every repo")
    parser.add_argument("--allow-dirty", action="store_true",
                        help="fast-forward even with uncommitted changes (git may refuse)")
    parser.add_argument("--dry-run", action="store_true",
                        help="report what would run without touching git")
    args = parser.parse_args()

    if not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    repos = parse_list(args.list)
    if args.only:
        wanted = {n.lower() for n in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("nothing to sync", file=sys.stderr)
        return 2

    if args.jobs > 1:
        with concurrent.futures.ThreadPoolExecutor(max_workers=args.jobs) as pool:
            results = list(pool.map(lambda r: sync(r, args), repos))
    else:
        results = [sync(r, args) for r in repos]

    width = max(len(r.name) for r in results)
    marks = {"updated": "PULL  ", "current": "OK    ", "skipped": "SKIP  ", "failed": "FAIL  "}
    for r in results:
        print(f"{marks[r.status]}{r.name:<{width}}  {r.detail}")

    failed = sum(1 for r in results if r.status == "failed")
    skipped = sum(1 for r in results if r.status == "skipped")
    updated = sum(1 for r in results if r.status == "updated")
    print(f"\n{updated} updated, {len(results) - updated - failed - skipped} already current, "
          f"{skipped} skipped, {failed} failed")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
