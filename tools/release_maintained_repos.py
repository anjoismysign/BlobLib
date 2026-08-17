#!/usr/bin/env python3
"""Commit, push, PR and merge one branch across every repo in bloblib-memory/maintained repos.md.

For each repo the pipeline is: checkout BRANCH -> stage -> commit MESSAGE -> push ->
open a pull request against the repo's default branch -> merge it. Each stage is skipped
when it has nothing to do (no changes to commit, branch already pushed, PR already open),
so rerunning after a partial failure resumes rather than duplicating work.

This pushes and merges, which is neither local nor easily undone, so it runs in --dry-run
unless you pass --yes. Stages can be stopped short with --no-pr (commit and push only) or
--no-merge (open the PR but leave it for review).

Merging uses a squash by default; --merge-method controls that. A repo is left alone and
reported when anything is ambiguous: a dirty tree with nothing staged to commit, a branch
that does not exist (unless --create), or a PR that is not mergeable.

Requires the `gh` CLI, authenticated, for the pull request stages.

Usage:
    python3 tools/release_maintained_repos.py BRANCH -m MESSAGE --yes
                                              [-l LIST] [--only NAME ...]
                                              [--create] [--all]
                                              [--title T] [--body B]
                                              [--merge-method squash|merge|rebase]
                                              [--no-pr] [--no-merge] [--admin]
                                              [--keep-branch] [--dry-run]
"""

from __future__ import annotations

import argparse
import os
import shutil
import subprocess
import sys
from dataclasses import dataclass, field
from pathlib import Path

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"
MERGE_FLAG = {"squash": "--squash", "merge": "--merge", "rebase": "--rebase"}


@dataclass
class Result:
    name: str
    status: str  # done | partial | nothing | skipped | failed
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


def checkout(repo: Path, branch: str, base: str, remote: str,
             args: argparse.Namespace, steps: list[str]) -> str | None:
    """Put the repo on `branch`. Returns an error message, or None on success."""
    current = out(repo, "rev-parse", "--abbrev-ref", "HEAD")
    if current == branch:
        return None

    # Changes made on the wrong branch: git refuses the switch when they would be
    # clobbered, and silently drags them along when they would not. Neither is what
    # the caller meant, so say so instead of guessing.
    if out(repo, "status", "--porcelain", "--untracked-files=no"):
        return (f"uncommitted changes on {current}, but {branch} is the target branch"
                " — commit or stash them first")

    if has_ref(repo, f"refs/heads/{branch}"):
        cmd = ("checkout", branch)
        planned, done = f"check out {branch}", f"checked out {branch}"
    elif has_ref(repo, f"refs/remotes/{remote}/{branch}"):
        cmd = ("checkout", "-b", branch, "--track", f"{remote}/{branch}")
        planned = f"check out {branch} tracking {remote}/{branch}"
        done = f"checked out {branch} tracking {remote}/{branch}"
    elif args.create:
        cmd = ("checkout", "-b", branch, base)
        planned, done = f"create {branch} from {base}", f"created {branch} from {base}"
    else:
        return f"no branch {branch} (pass --create to make one from {base})"

    if args.dry_run:
        steps.append(f"would {planned}")
        return None
    proc = git(repo, *cmd)
    if proc.returncode != 0:
        return f"checkout {branch} failed: {last_line(proc)}"
    steps.append(done)
    return None


def commit(repo: Path, args: argparse.Namespace, steps: list[str]) -> str | None:
    """Stage and commit. Returns an error message, or None (including 'nothing to do')."""
    # -u stages tracked modifications only; -A would sweep in stray untracked files.
    stage = ("add", "-A") if args.all else ("add", "-u")
    if not args.dry_run:
        proc = git(repo, *stage)
        if proc.returncode != 0:
            return f"staging failed: {last_line(proc)}"

    # --cached compares the index to HEAD: empty means there is nothing new to commit.
    pending = git(repo, "diff", "--cached", "--quiet").returncode != 0
    if args.dry_run:
        # Nothing was staged in a dry run, so ask the working tree instead.
        scope = () if args.all else ("--untracked-files=no",)
        pending = bool(out(repo, "status", "--porcelain", *scope))
        if pending:
            steps.append(f"would commit \"{args.message}\"")
        return None

    if not pending:
        return None
    proc = git(repo, "commit", "-m", args.message)
    if proc.returncode != 0:
        return f"commit failed: {last_line(proc)}"
    steps.append(f"committed \"{args.message}\"")
    return None


def push(repo: Path, branch: str, remote: str,
         args: argparse.Namespace, steps: list[str]) -> str | None:
    upstream = out(repo, "rev-parse", "--abbrev-ref", "--symbolic-full-name", "@{upstream}")
    if upstream and not out(repo, "rev-list", f"{upstream}..HEAD"):
        return None  # remote already has these commits

    if args.dry_run:
        steps.append(f"would push {branch} to {remote}")
        return None
    proc = git(repo, "push", "--set-upstream", remote, branch)
    if proc.returncode != 0:
        return f"push failed: {last_line(proc)}"
    steps.append(f"pushed {branch}")
    return None


def open_pr(repo: Path, branch: str, base: str,
            args: argparse.Namespace, steps: list[str]) -> tuple[str | None, str | None]:
    """Ensure a PR exists. Returns (error, pr_number)."""
    existing = run(repo, "gh", "pr", "list", "--head", branch, "--base", base,
                   "--state", "open", "--json", "number", "--jq", ".[0].number")
    if existing.returncode == 0 and existing.stdout.strip():
        number = existing.stdout.strip()
        steps.append(f"PR #{number} already open")
        return None, number

    if args.dry_run:
        steps.append(f"would open PR {branch} -> {base}")
        return None, None

    proc = run(repo, "gh", "pr", "create", "--base", base, "--head", branch,
               "--title", args.title or args.message,
               "--body", args.body or "")
    if proc.returncode != 0:
        return f"pr create failed: {last_line(proc)}", None

    number = out(repo, "rev-parse", "HEAD")  # placeholder if the lookup below fails
    lookup = run(repo, "gh", "pr", "view", branch, "--json", "number", "--jq", ".number")
    if lookup.returncode == 0 and lookup.stdout.strip():
        number = lookup.stdout.strip()
    steps.append(f"opened PR #{number}")
    return None, number


def merge_pr(repo: Path, branch: str, number: str | None,
             args: argparse.Namespace, steps: list[str]) -> str | None:
    if args.dry_run:
        steps.append(f"would merge PR ({args.merge_method})")
        return None

    cmd = ["gh", "pr", "merge", number or branch, MERGE_FLAG[args.merge_method]]
    if not args.keep_branch:
        cmd.append("--delete-branch")
    if args.admin:
        cmd.append("--admin")
    proc = run(repo, *cmd)
    if proc.returncode != 0:
        return f"merge failed: {last_line(proc)}"
    steps.append(f"merged ({args.merge_method})")
    return None


def release(repo: Path, args: argparse.Namespace) -> Result:
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
    if args.branch == base:
        return Result(name, "skipped",
                      detail=f"{args.branch} is the default branch — nothing to PR into")

    error = checkout(repo, args.branch, base, remote, args, steps)
    if error:
        benign = error.startswith(("no branch", "uncommitted changes"))
        return Result(name, "skipped" if benign else "failed", steps, error)

    error = commit(repo, args, steps)
    if error:
        return Result(name, "failed", steps, error)

    error = push(repo, args.branch, remote, args, steps)
    if error:
        return Result(name, "failed", steps, error)

    if args.no_pr:
        return Result(name, "done" if steps else "nothing", steps)

    error, number = open_pr(repo, args.branch, base, args, steps)
    if error:
        return Result(name, "partial", steps, error)

    if args.no_merge:
        return Result(name, "done" if steps else "nothing", steps)

    error = merge_pr(repo, args.branch, number, args, steps)
    if error:
        return Result(name, "partial", steps, error)

    return Result(name, "done" if steps else "nothing", steps)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("branch", help="branch to commit on and open the PR from, e.g. 1.701")
    parser.add_argument("-m", "--message", required=True, help="commit message")
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="limit to repos whose directory name matches (case-insensitive)")
    parser.add_argument("--create", action="store_true",
                        help="create the branch from the default branch where it is missing")
    parser.add_argument("--all", action="store_true",
                        help="stage untracked files too (default: tracked changes only)")
    parser.add_argument("--title", default=None, help="PR title (default: the commit message)")
    parser.add_argument("--body", default=None, help="PR body (default: empty)")
    parser.add_argument("--merge-method", choices=sorted(MERGE_FLAG), default="squash",
                        help="how to merge the PR (default: squash)")
    parser.add_argument("--keep-branch", action="store_true",
                        help="do not delete the branch after merging")
    parser.add_argument("--admin", action="store_true",
                        help="pass --admin to gh pr merge, bypassing required checks")
    parser.add_argument("--no-pr", action="store_true",
                        help="stop after pushing, opening no pull request")
    parser.add_argument("--no-merge", action="store_true",
                        help="open the pull request but do not merge it")
    parser.add_argument("--dry-run", action="store_true",
                        help="report what would happen, touching nothing")
    parser.add_argument("--yes", action="store_true",
                        help="actually push and merge (without it, this is a dry run)")
    args = parser.parse_args()

    # Pushing and merging cannot be undone from here, so acting requires saying so.
    if not args.yes:
        args.dry_run = True

    if not args.no_pr and shutil.which("gh") is None:
        print("gh not on PATH — install it, or pass --no-pr", file=sys.stderr)
        return 2
    if not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    repos = parse_list(args.list)
    if args.only:
        wanted = {n.lower() for n in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("nothing to release", file=sys.stderr)
        return 2

    if args.dry_run:
        print(f"dry run — nothing will be committed, pushed or merged "
              f"(pass --yes to act on {len(repos)} repo(s))\n")

    # Serial on purpose: these are network writes, and interleaved gh output is unreadable.
    results = [release(r, args) for r in repos]

    width = max(len(r.name) for r in results)
    marks = {"done": "DONE  ", "partial": "PART  ", "nothing": "NOOP  ",
             "skipped": "SKIP  ", "failed": "FAIL  "}
    for r in results:
        print(f"{marks[r.status]}{r.name:<{width}}  {r.line()}")

    tally = {k: sum(1 for r in results if r.status == k) for k in marks}
    print(f"\n{tally['done']} done, {tally['partial']} partial, {tally['nothing']} no-op, "
          f"{tally['skipped']} skipped, {tally['failed']} failed")
    return 1 if tally["failed"] or tally["partial"] else 0


if __name__ == "__main__":
    sys.exit(main())
