#!/usr/bin/env python3
"""Run `mvn clean package` on every repo listed in docs/maintained repos.md.

Each repo is built from its root pom.xml, so multi-module projects (e.g. BlobOutlaw)
are built as a whole reactor instead of module by module.

Each repo's required Java release is read from its pom and a matching JDK is selected
via /usr/libexec/java_home, so a repo targeting 25 is not built with the JDK that
happens to be on PATH ("invalid target release: 25"). Override with --java-home.

Usage:
    python3 tools/build_maintained_repos.py [-l LIST] [-j N] [--offline]
                                            [--skip-tests] [--dry-run]
                                            [--only NAME ...] [--log-dir DIR]
                                            [--java-home PATH]
"""

from __future__ import annotations

import argparse
import concurrent.futures
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

DEFAULT_LIST = Path(__file__).resolve().parent.parent / "docs" / "maintained repos.md"


@dataclass
class Result:
    repo: Path
    status: str  # ok | failed | skipped
    seconds: float
    detail: str = ""
    log: Path | None = None


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


def module_count(pom: Path) -> int:
    """Number of <modules>/<module> entries, i.e. > 0 means multi-module."""
    try:
        root = ET.parse(pom).getroot()
    except ET.ParseError:
        return 0
    modules = root.find(f"{MAVEN_NS}modules")
    if modules is None:
        modules = root.find("modules")  # pom without namespace
    return 0 if modules is None else len(list(modules))


def required_release(pom: Path) -> str | None:
    """Java release the pom asks for, from the usual compiler properties."""
    try:
        root = ET.parse(pom).getroot()
    except ET.ParseError:
        return None

    def find(tag: str):
        return root.find(f"{MAVEN_NS}{tag}") if root.find(f"{MAVEN_NS}{tag}") is not None \
            else root.find(tag)

    props = find("properties")
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


def jdk_home(release: str) -> Path | None:
    """Path to an installed JDK for `release`, via macOS java_home."""
    helper = Path("/usr/libexec/java_home")
    if not helper.is_file():
        return None
    proc = subprocess.run([str(helper), "-v", release],
                          capture_output=True, text=True)
    if proc.returncode != 0:
        return None
    path = Path(proc.stdout.strip())
    return path if path.is_dir() else None


def maven_env(repo: Path, pom: Path, args: argparse.Namespace) -> tuple[dict[str, str], str]:
    """Environment for the Maven run plus a note about the JDK chosen."""
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


def build(repo: Path, args: argparse.Namespace, log_dir: Path | None) -> Result:
    start = time.monotonic()

    if not repo.is_dir():
        return Result(repo, "skipped", 0.0, "directory not found")
    pom = repo / POM
    if not pom.is_file():
        return Result(repo, "skipped", 0.0, "no root pom.xml")

    modules = module_count(pom)
    kind = f"multi-module ({modules} modules)" if modules else "single module"

    try:
        cmd = maven_command(repo)
    except RuntimeError as exc:
        return Result(repo, "skipped", 0.0, str(exc))

    cmd += ["-B", "clean", "package"]
    if args.offline:
        cmd.append("-o")
    if args.skip_tests:
        cmd.append("-DskipTests")
    cmd += args.maven_args

    env, jdk_note = maven_env(repo, pom, args)
    kind = f"{kind}, {jdk_note}"

    if args.dry_run:
        print(f"[dry-run] {repo.name}: {kind} -> {' '.join(cmd)} (cwd={repo})")
        return Result(repo, "ok", 0.0, "dry run")

    print(f"==> {repo.name}: {kind}")
    log_path = None
    if log_dir is not None:
        log_dir.mkdir(parents=True, exist_ok=True)
        log_path = log_dir / f"{repo.name}.log"

    proc = subprocess.run(
        cmd,
        cwd=repo,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        errors="replace",
        env=env,
    )
    if log_path is not None:
        log_path.write_text(proc.stdout, encoding="utf-8")
    elif proc.returncode != 0 or args.verbose:
        sys.stdout.write(proc.stdout)

    seconds = time.monotonic() - start
    if proc.returncode == 0:
        return Result(repo, "ok", seconds, kind, log_path)

    tail = "\n".join(
        line for line in proc.stdout.splitlines() if "ERROR" in line
    ).splitlines()[-5:]
    detail = f"exit {proc.returncode}"
    if tail:
        detail += " | " + " / ".join(t.strip() for t in tail)
    return Result(repo, "failed", seconds, detail, log_path)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                        help="markdown file with one repo path per bullet")
    parser.add_argument("-j", "--jobs", type=int, default=1,
                        help="how many repos to build concurrently (default 1)")
    parser.add_argument("--offline", action="store_true", help="pass -o to Maven")
    parser.add_argument("--skip-tests", action="store_true", help="pass -DskipTests")
    parser.add_argument("--dry-run", action="store_true",
                        help="print what would run without building")
    parser.add_argument("--only", nargs="+", default=None, metavar="NAME",
                        help="build only repos whose directory name matches (case-insensitive)")
    parser.add_argument("--java-home", type=Path, default=None,
                        help="force this JDK for every repo instead of matching the pom's release")
    parser.add_argument("--log-dir", type=Path, default=None,
                        help="write per-repo build logs here instead of stdout")
    parser.add_argument("-v", "--verbose", action="store_true",
                        help="always print Maven output, not just on failure")
    parser.add_argument("maven_args", nargs="*", default=[],
                        help="extra args forwarded to Maven (use -- before them)")
    args = parser.parse_args()

    if not args.list.is_file():
        print(f"repo list not found: {args.list}", file=sys.stderr)
        return 2

    repos = parse_list(args.list)
    if args.only:
        wanted = {name.lower() for name in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]
    if not repos:
        print("nothing to build", file=sys.stderr)
        return 2

    if args.jobs > 1:
        with concurrent.futures.ThreadPoolExecutor(max_workers=args.jobs) as pool:
            results = list(pool.map(lambda r: build(r, args, args.log_dir), repos))
    else:
        results = [build(r, args, args.log_dir) for r in repos]

    print("\n=== summary ===")
    width = max(len(r.repo.name) for r in results)
    for r in results:
        mark = {"ok": "OK    ", "failed": "FAIL  ", "skipped": "SKIP  "}[r.status]
        print(f"{mark} {r.repo.name:<{width}}  {r.seconds:6.1f}s  {r.detail}")
        if r.log is not None and r.status == "failed":
            print(f"{'':<8}{'':<{width}}  log: {r.log}")

    failed = sum(1 for r in results if r.status == "failed")
    skipped = sum(1 for r in results if r.status == "skipped")
    print(f"\n{len(results) - failed - skipped} ok, {failed} failed, {skipped} skipped")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
