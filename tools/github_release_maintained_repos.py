#!/usr/bin/env python3
"""Publish a GitHub release for every repo in bloblib-memory/maintained repos.md.

Pass --repo PATH to release something else instead; `--repo .` releases BlobLib itself,
which is Gradle rather than Maven but follows the same tagging convention.

Each release is cut from the repo's default branch (`main` for some, `master` for
others), tagged after the version the build declares — `v1.0.16` for a pom at 1.0.16,
`v1.701` for a Gradle build at 1.701, which is the convention the releases already follow — and its body is the list of
commit subjects since the previous release:

    ## Changes since v1.0.15
    - fix: profile lookup on rejoin (a1b2c3d)
    - chore: bump BlobLib to 1.701 (e4f5g6h)

That is the same changelog the old .github/workflows/release.yml used to graft on after
the fact, computed here instead so the release is right the moment it is published.

Publishing is public and awkward to walk back, so it runs in --dry-run unless you pass
--yes. A repo is skipped, never guessed at, when anything is off: not on the default
branch, local commits not yet pushed (the tag would name a commit GitHub does not have),
a release for that version already published, or no commits at all since the last one.

Requires the `gh` CLI, authenticated.

Usage:
    python3 tools/github_release_maintained_repos.py --yes
                                                     [-l LIST] [--only NAME ...]
                                                     [--repo PATH ...]
                                                     [--tag-prefix v] [--draft]
                                                     [--prerelease] [--no-merges]
                                                     [--allow-existing] [--dry-run]
"""

from __future__ import annotations

import argparse
import os
import re
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from pathlib import Path

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"
MAVEN_NS = "{http://maven.apache.org/POM/4.0.0}"
POM = "pom.xml"


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
    proc = git(repo, *cmd)
    return proc.stdout.strip() if proc.returncode == 0 else ""


def last_line(proc: subprocess.CompletedProcess) -> str:
    lines = (proc.stderr or proc.stdout).strip().splitlines()
    return lines[-1].strip() if lines else f"exit {proc.returncode}"


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
    if git(repo, "remote", "set-head", remote, "-a").returncode == 0:
        head = out(repo, "symbolic-ref", "--short", f"refs/remotes/{remote}/HEAD")
        if head.startswith(f"{remote}/"):
            return head[len(remote) + 1:]
    for guess in ("main", "master"):
        if has_ref(repo, f"refs/remotes/{remote}/{guess}"):
            return guess
    return None


def pom_version(repo: Path) -> str | None:
    """The root pom's own <version>, falling back to the parent's when inherited."""
    pom = repo / POM
    if not pom.is_file():
        return None
    try:
        root = ET.parse(pom).getroot()
    except ET.ParseError:
        return None

    def child(parent, tag):
        if parent is None:
            return None
        el = parent.find(f"{MAVEN_NS}{tag}")
        return el if el is not None else parent.find(tag)

    version = child(root, "version")
    if version is None:
        version = child(child(root, "parent"), "version")
    if version is None or not version.text:
        return None
    text = version.text.strip()
    return text if text and not text.startswith("${") else None


def gradle_version(repo: Path) -> str | None:
    """The `version = "1.701"` line from a Gradle build, or gradle.properties.

    BlobLib itself is Gradle rather than Maven, and it releases under the same
    v<version> convention, so the version lookup has to handle both.
    """
    props = repo / "gradle.properties"
    if props.is_file():
        for line in props.read_text(encoding="utf-8", errors="replace").splitlines():
            match = re.fullmatch(r"\s*version\s*=\s*(.+?)\s*", line)
            if match:
                return match.group(1).strip("\"'")

    for name in ("build.gradle.kts", "build.gradle"):
        build = repo / name
        if not build.is_file():
            continue
        # Top level only: an indented `version =` belongs to a subproject or a
        # publication block, not to the root project.
        for line in build.read_text(encoding="utf-8", errors="replace").splitlines():
            match = re.fullmatch(r"version\s*=\s*[\"'](.+?)[\"']\s*", line)
            if match:
                return match.group(1)
    return None


def project_version(repo: Path) -> str | None:
    """Version to release, from whichever build system the repo uses."""
    return pom_version(repo) or gradle_version(repo)


def previous_release_tag(repo: Path, args: argparse.Namespace) -> str | None:
    """Tag of the latest published release, per GitHub — not per the local clone."""
    proc = run(repo, "gh", "release", "list", "--limit", "1",
               "--json", "tagName", "--jq", ".[0].tagName")
    if proc.returncode == 0 and proc.stdout.strip():
        return proc.stdout.strip()
    # No releases yet (or the repo predates them): fall back to the newest local tag.
    return out(repo, "describe", "--tags", "--abbrev=0") or None


def release_exists(repo: Path, tag: str) -> bool:
    return run(repo, "gh", "release", "view", tag, "--json", "tagName").returncode == 0


def changelog(repo: Path, since: str | None, args: argparse.Namespace) -> list[str]:
    """Commit subjects since `since`, newest first, as markdown bullets."""
    span = f"{since}..HEAD" if since else "HEAD"
    cmd = ["log", span, "--pretty=format:- %s (%h)"]
    if args.no_merges:
        cmd.append("--no-merges")
    text = out(repo, *cmd)
    return [line for line in text.splitlines() if line.strip()]


def body_for(tag: str, since: str | None, entries: list[str]) -> str:
    heading = f"## Changes since {since}" if since else "## Changes"
    return "\n".join([heading, "", *entries, ""])


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
        return Result(name, "skipped", detail=f"{where}, not the default branch `{base}`")

    # The release tags a commit on the remote, so anything unpushed would be released
    # as something GitHub cannot see.
    upstream = f"{remote}/{base}"
    git(repo, "fetch", "--quiet", remote, base)
    if not has_ref(repo, f"refs/remotes/{upstream}"):
        return Result(name, "skipped", detail=f"{base} has never been pushed")
    unpushed = out(repo, "rev-list", "--count", f"{upstream}..HEAD")
    if unpushed not in ("", "0"):
        return Result(name, "skipped",
                      detail=f"{unpushed} unpushed commit(s) — push before releasing")

    version = project_version(repo)
    if version is None:
        return Result(name, "skipped",
                      detail="cannot read a version from the root pom or Gradle build")
    tag = f"{args.tag_prefix}{version}"

    if release_exists(repo, tag):
        if not args.allow_existing:
            return Result(name, "nothing", detail=f"release {tag} already published")
        steps.append(f"{tag} exists, republishing body")

    since = previous_release_tag(repo, args)
    if since == tag:
        since = None  # the tag we are about to cut is already the latest; diff from scratch
    entries = changelog(repo, since, args)
    if not entries and not args.allow_empty:
        return Result(name, "nothing",
                      detail=f"no commits since {since}" if since else "no commits to release")

    body = body_for(tag, since, entries)
    summary = f"{len(entries)} commit(s) since {since}" if since else f"{len(entries)} commit(s)"

    if args.dry_run:
        steps.append(f"would publish {tag} from {base} ({summary})")
        return Result(name, "done", steps)

    cmd = ["gh", "release", "create", tag,
           "--target", base, "--title", version, "--notes", body]
    if args.draft:
        cmd.append("--draft")
    if args.prerelease:
        cmd.append("--prerelease")
    proc = run(repo, *cmd)
    if proc.returncode != 0:
        return Result(name, "failed", steps, f"release create failed: {last_line(proc)}")

    steps.append(f"published {tag} ({summary})")
    return Result(name, "done", steps)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="limit to repos whose directory name matches (case-insensitive)")
    parser.add_argument("--repo", nargs="+", default=None, type=Path, metavar="PATH",
                        help="release these repo paths instead of the list — `--repo .` "
                             "releases BlobLib itself")
    parser.add_argument("--tag-prefix", default="v",
                        help="prefix for the tag built from the pom version (default: v)")
    parser.add_argument("--draft", action="store_true", help="create the release as a draft")
    parser.add_argument("--prerelease", action="store_true", help="mark the release a prerelease")
    parser.add_argument("--no-merges", action="store_true",
                        help="leave merge commits out of the changelog")
    parser.add_argument("--allow-existing", action="store_true",
                        help="do not skip a version whose release is already published")
    parser.add_argument("--allow-empty", action="store_true",
                        help="release even when there are no commits since the last one")
    parser.add_argument("--dry-run", action="store_true",
                        help="report what would be published, touching nothing")
    parser.add_argument("--yes", action="store_true",
                        help="actually publish (without it, this is a dry run)")
    args = parser.parse_args()

    # Publishing a release is public and awkward to walk back.
    if not args.yes:
        args.dry_run = True

    if shutil.which("gh") is None:
        print("gh not on PATH — install it and authenticate", file=sys.stderr)
        return 2
    if not args.repo and not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    if args.repo:
        repos = [Path(os.path.expanduser(str(r))).resolve() for r in args.repo]
    else:
        repos = parse_list(args.list)
    if args.only:
        wanted = {n.lower() for n in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("nothing to release", file=sys.stderr)
        return 2

    if args.dry_run:
        print(f"dry run — nothing will be published "
              f"(pass --yes to act on {len(repos)} repo(s))\n")

    # Serial on purpose: these are network writes, and interleaved gh output is unreadable.
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
