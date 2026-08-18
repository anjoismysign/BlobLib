#!/usr/bin/env python3
"""Run `mvn deploy` on every repo listed in bloblib-memory/maintained repos.md.

Publishing goes to GitHub Packages, so each repo's root pom must carry a
<distributionManagement> repository whose id matches a <server> in ~/.m2/settings.xml
(id `github` by default). Credentials live in settings.xml, never here.

Deploying is a publish: once a version is up on GitHub Packages it cannot be replaced,
only deleted. So the script checks every repo BEFORE deploying any of them and refuses
the whole run if a single one looks wrong — chiefly a repo sitting on something other
than its default branch (`main` for some repos, `master` for others), which is how a
half-finished feature branch would otherwise get published as a release.

Preflight, all of which must pass for every repo:
  * the directory exists and has a root pom.xml
  * HEAD is on the repo's default branch, taken from the remote's HEAD (--allow-branch
    to permit named extras, --any-branch to drop the check entirely)
  * no uncommitted changes to tracked files — untracked files are ignored, since they
    are not part of what Maven publishes (--untracked to count them, --allow-dirty to
    drop the check)
  * the root pom declares distributionManagement, with a server id that settings.xml
    actually defines                     (--no-verify-auth)

Because it publishes, the script runs in --dry-run unless you pass --yes.

Usage:
    python3 tools/deploy_maintained_repos.py --yes [-l LIST] [--only NAME ...]
                                             [--skip-tests] [--offline]
                                             [--allow-branch NAME ...] [--any-branch]
                                             [--allow-dirty] [--untracked] [--no-verify-auth]
                                             [--java-home PATH] [--log-dir DIR]
                                             [--dry-run] [-v] [-- MAVEN_ARGS ...]
"""

from __future__ import annotations

import argparse
import os
import shutil
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path

POM = "pom.xml"
MAVEN_NS = "{http://maven.apache.org/POM/4.0.0}"
SETTINGS_NS = "{http://maven.apache.org/SETTINGS/1.0.0}"

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "bloblib-memory" / "maintained repos.md"
SETTINGS = Path.home() / ".m2" / "settings.xml"


@dataclass
class Check:
    """Outcome of the preflight for one repo."""
    repo: Path
    ok: bool
    detail: str
    branch: str = "?"
    server_id: str = ""


@dataclass
class Result:
    repo: Path
    status: str  # ok | failed
    seconds: float
    detail: str = ""
    log: Path | None = None


# --------------------------------------------------------------------------- list

def parse_list(path: Path) -> list[Path]:
    repos: list[Path] = []
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        if line.startswith(("- ", "* ", "+ ")):
            line = line[2:].strip()
        line = line.strip("`").strip()
        if not line:
            continue
        repos.append(Path(os.path.expanduser(line)))
    return repos


# ---------------------------------------------------------------------------- git

def git(repo: Path, *cmd: str) -> subprocess.CompletedProcess:
    return subprocess.run(["git", "-C", str(repo), *cmd], capture_output=True, text=True)


def out(repo: Path, *cmd: str) -> str:
    proc = git(repo, *cmd)
    return proc.stdout.strip() if proc.returncode == 0 else ""


def has_ref(repo: Path, ref: str) -> bool:
    return git(repo, "show-ref", "--verify", "--quiet", ref).returncode == 0


def default_branch(repo: Path, remote: str = "origin") -> str | None:
    """Branch the remote's HEAD points at, e.g. `main` for one repo, `master` for another."""
    head = out(repo, "symbolic-ref", "--short", f"refs/remotes/{remote}/HEAD")
    if head.startswith(f"{remote}/"):
        return head[len(remote) + 1:]
    # refs/remotes/<remote>/HEAD is only written at clone time and is often absent.
    if git(repo, "remote", "set-head", remote, "-a").returncode == 0:
        head = out(repo, "symbolic-ref", "--short", f"refs/remotes/{remote}/HEAD")
        if head.startswith(f"{remote}/"):
            return head[len(remote) + 1:]
    for guess in ("main", "master"):
        if has_ref(repo, f"refs/remotes/{remote}/{guess}"):
            return guess
    return None


# ---------------------------------------------------------------------------- pom

def _find(parent, tag: str):
    """Child `tag`, with or without the Maven namespace."""
    if parent is None:
        return None
    el = parent.find(f"{MAVEN_NS}{tag}")
    return el if el is not None else parent.find(tag)


def deploy_target(pom: Path) -> tuple[str, str] | None:
    """(server id, url) from <distributionManagement>, or None when absent."""
    try:
        root = ET.parse(pom).getroot()
    except ET.ParseError:
        return None
    dm = _find(root, "distributionManagement")
    repo = _find(dm, "repository")
    if repo is None:
        return None
    rid = _find(repo, "id")
    url = _find(repo, "url")
    return ((rid.text or "").strip() if rid is not None else "",
            (url.text or "").strip() if url is not None else "")


def settings_server_ids(settings: Path) -> set[str]:
    try:
        root = ET.parse(settings).getroot()
    except (ET.ParseError, OSError):
        return set()
    ids: set[str] = set()
    servers = _find_settings(root, "servers")
    if servers is None:
        return ids
    for server in servers:
        sid = _find_settings(server, "id")
        if sid is not None and sid.text:
            ids.add(sid.text.strip())
    return ids


def _find_settings(parent, tag: str):
    el = parent.find(f"{SETTINGS_NS}{tag}")
    return el if el is not None else parent.find(tag)


def required_release(pom: Path) -> str | None:
    """Java release the pom asks for, from the usual compiler properties."""
    try:
        root = ET.parse(pom).getroot()
    except ET.ParseError:
        return None
    props = _find(root, "properties")
    if props is None:
        return None
    for name in ("maven.compiler.release", "maven.compiler.target",
                 "maven.compiler.source", "java.version"):
        el = _find(props, name)
        if el is not None and el.text and el.text.strip().isdigit():
            return el.text.strip()
    return None


def jdk_home(release: str) -> Path | None:
    """Path to an installed JDK for `release`, via macOS java_home."""
    helper = Path("/usr/libexec/java_home")
    if not helper.is_file():
        return None
    proc = subprocess.run([str(helper), "-v", release], capture_output=True, text=True)
    if proc.returncode != 0:
        return None
    path = Path(proc.stdout.strip())
    return path if path.is_dir() else None


def maven_env(pom: Path, args: argparse.Namespace) -> tuple[dict[str, str], str]:
    env = os.environ.copy()
    if args.java_home:
        env["JAVA_HOME"] = str(args.java_home)
        return env, f"JDK {args.java_home}"
    release = required_release(pom)
    if release is None:
        return env, "JDK default"
    home = jdk_home(release)
    if home is None:
        return env, f"needs Java {release}, none installed — using default JDK"
    env["JAVA_HOME"] = str(home)
    return env, f"JDK {release}"


def maven_command(repo: Path) -> list[str]:
    wrapper = repo / ("mvnw.cmd" if os.name == "nt" else "mvnw")
    if wrapper.is_file() and os.access(wrapper, os.X_OK):
        return [str(wrapper)]
    mvn = shutil.which("mvn")
    if mvn is None:
        raise RuntimeError("neither ./mvnw nor `mvn` on PATH")
    return [mvn]


# ---------------------------------------------------------------------- preflight

def preflight(repo: Path, args: argparse.Namespace, server_ids: set[str]) -> Check:
    if not repo.is_dir():
        return Check(repo, False, "directory not found")
    pom = repo / POM
    if not pom.is_file():
        return Check(repo, False, "no root pom.xml")
    if git(repo, "rev-parse", "--git-dir").returncode != 0:
        return Check(repo, False, "not a git repository")

    branch = out(repo, "rev-parse", "--abbrev-ref", "HEAD") or "?"
    if not args.any_branch:
        if branch == "HEAD":
            return Check(repo, False, "detached HEAD", branch)
        expected = default_branch(repo)
        allowed = set(args.allow_branch or [])
        if expected:
            allowed.add(expected)
        else:
            allowed |= {"main", "master"}
        if branch not in allowed:
            wanted = ", ".join(sorted(allowed))
            return Check(repo, False, f"on `{branch}`, expected {wanted}", branch)

    # Untracked files (a stray .DS_Store, a scratch script) are not part of what Maven
    # publishes, so they do not block; modifications to tracked files are, and do.
    scope = () if args.untracked else ("--untracked-files=no",)
    if not args.allow_dirty and out(repo, "status", "--porcelain", *scope):
        what = "uncommitted changes" if args.untracked else "uncommitted changes to tracked files"
        return Check(repo, False, what, branch)

    target = deploy_target(pom)
    if target is None:
        return Check(repo, False, "root pom has no <distributionManagement>", branch)
    server_id, url = target
    if not url:
        return Check(repo, False, "<distributionManagement> has no <url>", branch)
    if not args.no_verify_auth and server_id not in server_ids:
        return Check(repo, False,
                     f"server id `{server_id}` not in {SETTINGS}", branch, server_id)

    return Check(repo, True, url, branch, server_id)


# ------------------------------------------------------------------------- deploy

def deploy(repo: Path, args: argparse.Namespace, log_dir: Path | None) -> Result:
    start = time.monotonic()
    pom = repo / POM
    try:
        cmd = maven_command(repo)
    except RuntimeError as exc:
        return Result(repo, "failed", 0.0, str(exc))

    cmd += ["-B", "clean", "deploy"]
    if args.offline:
        cmd.append("-o")
    if args.skip_tests:
        cmd.append("-DskipTests")
    cmd += args.maven_args

    env, jdk_note = maven_env(pom, args)

    if args.dry_run:
        print(f"[dry-run] {repo.name}: {jdk_note} -> {' '.join(cmd)} (cwd={repo})")
        return Result(repo, "ok", 0.0, "dry run")

    print(f"==> {repo.name}: {jdk_note}")
    log_path = None
    if log_dir is not None:
        log_dir.mkdir(parents=True, exist_ok=True)
        log_path = log_dir / f"{repo.name}.log"

    proc = subprocess.run(cmd, cwd=repo, stdout=subprocess.PIPE,
                          stderr=subprocess.STDOUT, text=True,
                          errors="replace", env=env)
    if log_path is not None:
        log_path.write_text(proc.stdout, encoding="utf-8")
    elif proc.returncode != 0 or args.verbose:
        sys.stdout.write(proc.stdout)

    seconds = time.monotonic() - start
    if proc.returncode == 0:
        return Result(repo, "ok", seconds, jdk_note, log_path)

    tail = [line.strip() for line in proc.stdout.splitlines() if "ERROR" in line][-5:]
    detail = f"exit {proc.returncode}"
    if tail:
        detail += " | " + " / ".join(tail)
    return Result(repo, "failed", seconds, detail, log_path)


# --------------------------------------------------------------------------- main

def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("--yes", action="store_true",
                        help="actually deploy; without it the script only dry-runs")
    parser.add_argument("--dry-run", action="store_true",
                        help="print what would run without deploying")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="deploy only repos whose directory name matches (case-insensitive)")
    parser.add_argument("--allow-branch", nargs="+", default=None, metavar="NAME",
                        help="extra branch names to accept besides the repo's default branch")
    parser.add_argument("--any-branch", action="store_true",
                        help="skip the branch check entirely")
    parser.add_argument("--allow-dirty", action="store_true",
                        help="deploy even with uncommitted changes in the tree")
    parser.add_argument("--untracked", action="store_true",
                        help="also treat untracked files as a dirty tree (default: ignore them)")
    parser.add_argument("--no-verify-auth", action="store_true",
                        help="do not check the pom's server id against ~/.m2/settings.xml")
    parser.add_argument("--skip-tests", action="store_true", help="pass -DskipTests")
    parser.add_argument("--offline", action="store_true", help="pass -o to Maven")
    parser.add_argument("--java-home", type=Path, default=None,
                        help="force this JDK for every repo instead of matching the pom's release")
    parser.add_argument("--log-dir", type=Path, default=None,
                        help="write per-repo deploy logs here instead of stdout")
    parser.add_argument("-v", "--verbose", action="store_true",
                        help="always print Maven output, not just on failure")
    parser.add_argument("maven_args", nargs="*", default=[],
                        help="extra args forwarded to Maven (use -- before them)")
    args = parser.parse_args()

    if not args.yes:
        args.dry_run = True

    if not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    repos = parse_list(args.list)
    if args.only:
        wanted = {name.lower() for name in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("nothing to deploy", file=sys.stderr)
        return 2

    server_ids = settings_server_ids(SETTINGS)
    if not server_ids and not args.no_verify_auth:
        print(f"no <server> entries found in {SETTINGS} — "
              f"deploys would fail to authenticate (use --no-verify-auth to proceed anyway)",
              file=sys.stderr)
        return 2

    print(f"=== preflight ({len(repos)} repos) ===")
    checks = [preflight(r, args, server_ids) for r in repos]
    width = max(len(c.repo.name) for c in checks)
    for c in checks:
        mark = "OK    " if c.ok else "BLOCK "
        print(f"{mark} {c.repo.name:<{width}}  {c.branch:<10}  {c.detail}")

    blocked = [c for c in checks if not c.ok]
    if blocked:
        print(f"\nrefusing to deploy: {len(blocked)} of {len(checks)} repos failed preflight",
              file=sys.stderr)
        print("nothing was published.", file=sys.stderr)
        return 1

    if args.dry_run and not args.yes:
        print("\ndry run (pass --yes to deploy for real)")

    print(f"\n=== deploy ({len(repos)} repos) ===")
    results = [deploy(r, args, args.log_dir) for r in repos]

    print("\n=== summary ===")
    for r in results:
        mark = "OK    " if r.status == "ok" else "FAIL  "
        print(f"{mark} {r.repo.name:<{width}}  {r.seconds:6.1f}s  {r.detail}")
        if r.log is not None and r.status == "failed":
            print(f"{'':<8}{'':<{width}}  log: {r.log}")

    failed = sum(1 for r in results if r.status == "failed")
    print(f"\n{len(results) - failed} ok, {failed} failed")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
