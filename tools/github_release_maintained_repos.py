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

These are Minecraft plugins, so a release also has to carry the plugin jar — GitHub's
automatic source tarballs are not something a server operator can install. Each repo is
built first (Maven `package`, or Gradle `shadowJar` for BlobLib) and the resulting jar is
attached. The jar is looked for in every module that ships a plugin.yml, so a multi-module
repo attaches its plugin module's jar rather than its API library's, and never the
`original-*.jar` that maven-shade leaves behind without its bundled dependencies.
Use --no-build to attach what is already built, or --no-assets for a source-only release.

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
                                                     [--no-build] [--no-assets]
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


# ------------------------------------------------------------------ build + jars

def plugin_modules(repo: Path) -> list[Path]:
    """Directories that build a Minecraft plugin, i.e. that ship a plugin.yml.

    Multi-module repos keep the plugin in one module (`bloboutlaw-paper`,
    `blobproperties-plugin`) and libraries in the others; the plugin jar is the one a
    server operator installs, so that is what a release has to carry.
    """
    modules = []
    for name in ("plugin.yml", "paper-plugin.yml"):
        for found in repo.rglob(f"src/main/resources/{name}"):
            module = found.parent.parent.parent.parent
            if module not in modules:
                modules.append(module)
    return modules or [repo]


def jar_for(module: Path, version: str) -> Path | None:
    """The built plugin jar for `module`, or None when it has not been built.

    `original-*.jar` is what maven-shade-plugin renames the pre-shade jar to — it is
    missing every bundled dependency, so shipping it would produce a plugin that
    NoClassDefFoundErrors on load. Sources and javadoc jars are not plugins either.
    """
    for out_dir in (module / "target", module / "build" / "libs"):
        if not out_dir.is_dir():
            continue
        candidates = [
            jar for jar in sorted(out_dir.glob(f"*{version}.jar"))
            if not jar.name.startswith("original-")
            and not jar.name.endswith(("-sources.jar", "-javadoc.jar"))
        ]
        if candidates:
            # Shortest name wins: `BlobLib-1.701.jar` over `BlobLib-1.701-all.jar`.
            return min(candidates, key=lambda jar: len(jar.name))
    return None


def required_release(repo: Path) -> str | None:
    """Java release the root pom asks for, so a repo targeting 25 is not built with 17."""
    pom = repo / POM
    if not pom.is_file():
        return None
    try:
        root = ET.parse(pom).getroot()
    except ET.ParseError:
        return None
    props = root.find(f"{MAVEN_NS}properties")
    if props is None:
        props = root.find("properties")
    if props is None:
        return None
    for name in ("maven.compiler.release", "maven.compiler.target",
                 "maven.compiler.source", "java.version"):
        el = props.find(f"{MAVEN_NS}{name}")
        if el is None:
            el = props.find(name)
        if el is not None and el.text and el.text.strip().isdigit():
            return el.text.strip()
    return None


def build_env(repo: Path) -> dict[str, str]:
    env = os.environ.copy()
    release = required_release(repo)
    if release is None:
        return env
    helper = Path("/usr/libexec/java_home")
    if not helper.is_file():
        return env
    proc = subprocess.run([str(helper), "-v", release], capture_output=True, text=True)
    if proc.returncode == 0 and Path(proc.stdout.strip()).is_dir():
        env["JAVA_HOME"] = proc.stdout.strip()
    return env


def build_command(repo: Path) -> list[str] | None:
    """How to produce this repo's jars — Maven for the plugins, Gradle for BlobLib."""
    if (repo / POM).is_file():
        wrapper = repo / "mvnw"
        base = [str(wrapper)] if wrapper.is_file() and os.access(wrapper, os.X_OK) \
            else ([shutil.which("mvn")] if shutil.which("mvn") else None)
        if base is None:
            return None
        return [*base, "-B", "clean", "package", "-DskipTests"]

    for build_file in ("build.gradle.kts", "build.gradle"):
        if not (repo / build_file).is_file():
            continue
        wrapper = repo / "gradlew"
        base = [str(wrapper)] if wrapper.is_file() and os.access(wrapper, os.X_OK) \
            else ([shutil.which("gradle")] if shutil.which("gradle") else None)
        if base is None:
            return None
        # The shadow plugin's fat jar is the plugin; a plain `jar` would omit the
        # bundled dependencies exactly like Maven's original-*.jar does.
        text = (repo / build_file).read_text(encoding="utf-8", errors="replace")
        task = "shadowJar" if "shadow" in text else "assemble"
        return [*base, task, "-x", "test"]
    return None


def build(repo: Path, steps: list[str], args: argparse.Namespace) -> str | None:
    cmd = build_command(repo)
    if cmd is None:
        return "no Maven or Gradle build found (or its tool is not on PATH)"
    if args.dry_run:
        steps.append(f"would build ({Path(cmd[0]).name})")
        return None
    proc = subprocess.run(cmd, cwd=repo, capture_output=True, text=True,
                          errors="replace", env=build_env(repo))
    if proc.returncode != 0:
        tail = [ln.strip() for ln in proc.stdout.splitlines()
                if "ERROR" in ln or "FAILED" in ln][-2:]
        return f"build failed: {' / '.join(tail) or last_line(proc)}"
    steps.append("built")
    return None


def assets_for(repo: Path, version: str, steps: list[str],
               args: argparse.Namespace) -> tuple[list[Path], str | None]:
    """The jars to attach to the release, or an error explaining why there are none."""
    jars, missing = [], []
    for module in plugin_modules(repo):
        jar = jar_for(module, version)
        if jar is None:
            missing.append(module.name)
        else:
            jars.append(jar)
    if missing and not args.dry_run:
        return [], f"no built jar for {', '.join(missing)} (expected *{version}.jar)"
    if jars:
        steps.append(f"jar: {', '.join(j.name for j in jars)}")
    elif args.dry_run:
        steps.append(f"jar: not built yet (would attach *{version}.jar)")
    return jars, None


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

    exists = release_exists(repo, tag)
    if exists:
        if not args.allow_existing:
            return Result(name, "nothing", detail=f"release {tag} already published")
        steps.append(f"{tag} exists, attaching assets")

    since = previous_release_tag(repo, args)
    if since == tag:
        # The tag is already the latest release, so it is not a starting point for its
        # own changelog. Nothing is rewritten in that case anyway — only assets go up.
        since = None
    entries = [] if exists else changelog(repo, since, args)
    if not entries and not args.allow_empty and not exists:
        return Result(name, "nothing",
                      detail=f"no commits since {since}" if since else "no commits to release")

    body = body_for(tag, since, entries)
    summary = f"{len(entries)} commit(s) since {since}" if since else f"{len(entries)} commit(s)"

    # These are Minecraft plugins: a release whose only downloads are GitHub's source
    # tarballs is useless to a server operator, so the jar is built and attached.
    jars: list[Path] = []
    if not args.no_assets:
        if not args.no_build:
            error = build(repo, steps, args)
            if error:
                return Result(name, "failed", steps, error)
        jars, error = assets_for(repo, version, steps, args)
        if error:
            return Result(name, "failed", steps, error)

    if args.dry_run:
        # Attaching to a release that is already out says nothing about the changelog:
        # its body was written when it was published and is left alone.
        steps.append(f"would attach to existing {tag}" if exists
                     else f"would publish {tag} from {base} ({summary})")
        return Result(name, "done", steps)

    if exists:
        # The release is already out; only its assets are missing.
        proc = run(repo, "gh", "release", "upload", tag, *[str(j) for j in jars], "--clobber")
        if proc.returncode != 0:
            return Result(name, "failed", steps, f"asset upload failed: {last_line(proc)}")
        steps.append(f"attached to existing {tag}")
        return Result(name, "done", steps)

    cmd = ["gh", "release", "create", tag,
           "--target", base, "--title", version, "--notes", body,
           *[str(j) for j in jars]]
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
    parser.add_argument("--no-build", action="store_true",
                        help="attach the jars already in target/ or build/libs without rebuilding")
    parser.add_argument("--no-assets", action="store_true",
                        help="publish source-only, attaching no jar at all")
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
