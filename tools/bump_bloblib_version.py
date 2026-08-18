#!/usr/bin/env python3
"""Roll every maintained repo onto a new BlobLib version, build 0.

This is the "a new BlobLib version shipped" tool: it does both halves of the move in one
branch per repo --

  1. the io.github.anjoismysign:bloblib <dependency> version (set_bloblib_version.py's job)
  2. the repo's own version, reset to <bloblib-version>.0 (set_own_version.py's job, with
     the build number forced to 0 -- a new BlobLib major.minor always starts a repo's
     build count over, never carrying forward the previous minor's build number)

Both steps import their logic from set_bloblib_version.py and set_own_version.py rather
than reimplementing it, so this script cannot drift from what running either one by hand
would do. Branching is done once per repo up front (same rules as those two scripts:
stale-branch refusal, --rebase-existing, --allow-dirty, --no-branch), then both rewrites
land on it together.

Usage:
    python3 tools/bump_bloblib_version.py 1.702 [-l LIST] [--only NAME ...] [--dry-run]
                                                [--base BRANCH] [--allow-dirty]
                                                [--rebase-existing] [--no-branch]
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import github_release_maintained_repos as shared  # noqa: E402
import set_bloblib_version as dep  # noqa: E402
import set_own_version as own  # noqa: E402

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("bloblib_version", help="new BlobLib version, e.g. 1.702")
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

    repos = shared.parse_list(args.list)
    if args.only:
        wanted = {n.lower() for n in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("nothing to do", file=sys.stderr)
        return 2

    new_own_version = f"{args.bloblib_version}.0"
    problems = 0

    for repo in repos:
        if not repo.is_dir():
            print(f"SKIP  {repo.name}: directory not found")
            problems += 1
            continue

        if not args.no_branch:
            # Branch once, named after the repo's new own version -- the branch a PR
            # for "move to BlobLib 1.702" would be cut on.
            ok, message = dep.make_branch(repo, new_own_version, args)
            print(f"{'GIT   ' if ok else 'SKIP  '}{repo.name}: {message}")
            if not ok:
                problems += 1
                continue  # never edit poms on a branch we did not establish

        dep_targets = [p for p in dep.poms(repo) if dep.declares_bloblib(p)]
        if not dep_targets:
            print(f"WARN  {repo.name}: no pom declares {dep.GROUP_ID}:{dep.ARTIFACT_ID}")
            problems += 1
        for pom in dep_targets:
            rel = pom.relative_to(repo)
            outcome = dep.update_pom(pom, args.bloblib_version, args.dry_run)
            prefix = "DRY   " if args.dry_run else "SET   "
            if "skipped" in outcome or "unexpected" in outcome or "by hand" in outcome:
                prefix = "WARN  "
                problems += 1
            print(f"{prefix}{repo.name}/{rel} (dependency): {outcome}")

        current = shared.project_version(repo)
        if current is None:
            print(f"WARN  {repo.name}: could not read current own version")
            problems += 1
            continue
        if current == new_own_version:
            print(f"SKIP  {repo.name}: own version already {new_own_version}")
            continue

        for ok, message in own.update_repo(repo, new_own_version, current, args.dry_run):
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
