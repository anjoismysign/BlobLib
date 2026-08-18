#!/usr/bin/env python3
"""Commit and push straight to the default branch of every repo in bloblib-memory/maintained repos.md.

The counterpart to release_maintained_repos.py: that one carries a feature branch through
a pull request, this one is for work already done in place on `main`/`master` — a version
bump, a pom edit — where a PR would be ceremony. The pipeline per repo is
stage -> commit MESSAGE -> push, and each stage is skipped when it has nothing to do, so
rerunning after a partial failure resumes instead of duplicating.

The default branch is read from the remote's HEAD, so a repo on `master` and a repo on
`main` both work. A repo checked out on anything else is left alone and reported — this
tool never switches branches, because the whole point is committing what is in the tree
where it already is.

Pushing to the default branch is neither local nor easily undone, so it runs in --dry-run
unless you pass --yes. A branch behind its upstream is skipped rather than force-pushed;
--pull fast-forwards it first (never a merge, never a rebase) and pushes then.

Usage:
    python3 tools/push_maintained_repos.py -m MESSAGE --yes
                                           [-l LIST] [--only NAME ...]
                                           [--all] [--pull] [--dry-run]
"""

from __future__ import annotations

import argparse
import os
import subprocess
import sys
from dataclasses import dataclass, field
from pathlib import Path

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"


@dataclass
class Result:
    name: str
    status: str  # done | nothing | skipped | failed
    steps: list[str] = field(default_factory=list)
    detail: str = ""

    def line(self) -> str:
        trail = ", ".join(self.steps)
        if self.detail:
            trail = f"{trail} — {self.detail}" if trail else self.detail
        return trail or "nothing to do"


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


def run(repo: Path, *cmd: str) -> subprocess.CompletedProcess:
    return subprocess.run(cmd, cwd=repo, capture_output=True, text=True)


def git(repo: Path, *cmd: str) -> subprocess.CompletedProcess:
    return run(repo, "git", "-C", str(repo), *cmd)


def out(repo: Path, *cmd: str) -> str:
    return git(repo, *cmd).stdout.strip()


def last_line(proc: subprocess.CompletedProcess) -> str:
    lines = (proc.stderr or proc.stdout).strip().splitlines()
    return lines[-1].strip() if lines else f"exit {proc.returncode}"


def remote_name(repo: Path) -> str | None:
    remotes = out(repo, "remote").splitlines()
    if not remotes:
        return None
    return "origin" if "origin" in remotes else remotes[0].strip()


def has_ref(repo: Path, ref: str) -> bool:
    return git(repo, "rev-parse", "--verify", "--quiet", ref).returncode == 0


def default_branch(repo: Path, remote: str) -> str | None:
    """Branch the remote's HEAD points at — `main` for some repos, `master` for others."""
    head = out(repo, "symbolic-ref", "--short", f"refs/remotes/{remote}/HEAD")
    if head.startswith(f"{remote}/"):
        return head[len(remote) + 1:]
    if git(repo, "remote", "set-head", remote, "-a").returncode == 0:
        head = out(repo, "symbolic-ref", "--short", f"refs/remotes/{remote}/HEAD")
        if head.startswith(f"{remote}/"):
            return head[len(remote) + 1:]
    for guess in ("main", "master"):
        if has_ref(repo, f"refs/remotes/{remote}/{guess}"):
            return guess
    return None


def commit(repo: Path, args: argparse.Namespace, steps: list[str]) -> str | None:
    """Stage and commit. Returns an error message, or None (including 'nothing to do')."""
    # -u stages tracked modifications only; -A would sweep in stray untracked files.
    stage = ("add", "-A") if args.all else ("add", "-u")
    if args.dry_run:
        # Nothing gets staged in a dry run, so ask the working tree instead.
        scope = () if args.all else ("--untracked-files=no",)
        if out(repo, "status", "--porcelain", *scope):
            steps.append(f"would commit \"{args.message}\"")
        return None

    proc = git(repo, *stage)
    if proc.returncode != 0:
        return f"staging failed: {last_line(proc)}"
    # --cached compares the index to HEAD: empty means there is nothing new to commit.
    if git(repo, "diff", "--cached", "--quiet").returncode == 0:
        return None
    proc = git(repo, "commit", "-m", args.message)
    if proc.returncode != 0:
        return f"commit failed: {last_line(proc)}"
    steps.append(f"committed \"{args.message}\"")
    return None


def sync(repo: Path, branch: str, remote: str,
         args: argparse.Namespace, steps: list[str]) -> str | None:
    """Make sure the local branch is not behind its upstream. Returns an error, or None."""
    upstream = f"{remote}/{branch}"
    if not has_ref(repo, f"refs/remotes/{upstream}"):
        return None  # nothing published yet; the push will create it

    git(repo, "fetch", "--quiet", remote, branch)
    behind = out(repo, "rev-list", "--count", f"HEAD..{upstream}")
    if behind in ("", "0"):
        return None

    ahead = out(repo, "rev-list", "--count", f"{upstream}..HEAD")
    if not args.pull:
        return (f"{branch} is {behind} commit(s) behind {upstream}"
                " — pass --pull to fast-forward first")
    if ahead not in ("", "0"):
        # Diverged: a fast-forward is impossible and anything else rewrites history.
        return (f"{branch} has diverged from {upstream}"
                f" ({ahead} ahead, {behind} behind) — resolve it by hand")

    if args.dry_run:
        steps.append(f"would fast-forward {behind} commit(s) from {upstream}")
        return None
    proc = git(repo, "merge", "--ff-only", upstream)
    if proc.returncode != 0:
        return f"fast-forward failed: {last_line(proc)}"
    steps.append(f"fast-forwarded {behind} commit(s)")
    return None


def push(repo: Path, branch: str, remote: str, pending: bool,
         args: argparse.Namespace, steps: list[str]) -> str | None:
    upstream = f"{remote}/{branch}"
    unpushed = out(repo, "rev-list", f"{upstream}..HEAD") \
        if has_ref(repo, f"refs/remotes/{upstream}") else "unpublished"
    # In a dry run the commit was never made, so `pending` stands in for the commits
    # that would exist by the time the push happened.
    if not unpushed and not (args.dry_run and pending):
        return None  # remote already has these commits

    if args.dry_run:
        steps.append(f"would push {branch} to {remote}")
        return None
    proc = git(repo, "push", "--set-upstream", remote, branch)
    if proc.returncode != 0:
        return f"push failed: {last_line(proc)}"
    steps.append(f"pushed {branch}")
    return None


def publish(repo: Path, args: argparse.Namespace) -> Result:
    name = repo.name
    steps: list[str] = []

    if not repo.is_dir():
        return Result(name, "skipped", detail="directory not found")
    if git(repo, "rev-parse", "--git-dir").returncode != 0:
        return Result(name, "skipped", detail="not a git repository")
    remote = remote_name(repo)
    if remote is None:
        return Result(name, "skipped", detail="no remote configured")

    base = default_branch(repo, remote)
    if base is None:
        return Result(name, "skipped", detail=f"cannot tell {remote}'s default branch")

    current = out(repo, "rev-parse", "--abbrev-ref", "HEAD")
    if current != base:
        where = "detached HEAD" if current == "HEAD" else f"on `{current}`"
        return Result(name, "skipped", detail=f"{where}, not the default branch `{base}`"
                                              " — use release_maintained_repos.py for branches")

    error = commit(repo, args, steps)
    if error:
        return Result(name, "failed", steps, error)
    pending = any(step.startswith("would commit") or step.startswith("committed")
                  for step in steps)

    error = sync(repo, base, remote, args, steps)
    if error:
        return Result(name, "skipped", steps, error)

    error = push(repo, base, remote, pending, args, steps)
    if error:
        return Result(name, "failed", steps, error)

    return Result(name, "done" if steps else "nothing", steps)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-m", "--message", required=True, help="commit message")
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="limit to repos whose directory name matches (case-insensitive)")
    parser.add_argument("--all", action="store_true",
                        help="stage untracked files too (default: tracked changes only)")
    parser.add_argument("--pull", action="store_true",
                        help="fast-forward from the remote before pushing when behind")
    parser.add_argument("--dry-run", action="store_true",
                        help="report what would happen, touching nothing")
    parser.add_argument("--yes", action="store_true",
                        help="actually commit and push (without it, this is a dry run)")
    args = parser.parse_args()

    # Committing and pushing to the default branch cannot be undone from here.
    if not args.yes:
        args.dry_run = True

    if not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    repos = parse_list(args.list)
    if args.only:
        wanted = {n.lower() for n in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("nothing to push", file=sys.stderr)
        return 2

    if args.dry_run:
        print(f"dry run — nothing will be committed or pushed "
              f"(pass --yes to act on {len(repos)} repo(s))\n")

    # Serial on purpose: these are network writes, and interleaved output is unreadable.
    results = [publish(r, args) for r in repos]

    width = max(len(r.name) for r in results)
    marks = {"done": "DONE  ", "nothing": "NOOP  ", "skipped": "SKIP  ", "failed": "FAIL  "}
    for r in results:
        print(f"{marks[r.status]}{r.name:<{width}}  {r.line()}")

    tally = {k: sum(1 for r in results if r.status == k) for k in marks}
    print(f"\n{tally['done']} done, {tally['nothing']} no-op, "
          f"{tally['skipped']} skipped, {tally['failed']} failed")
    return 1 if tally["failed"] else 0


if __name__ == "__main__":
    sys.exit(main())
