#!/usr/bin/env python3
"""Publish a GitHub release for BlobLib itself.

Same machinery as github_release_maintained_repos.py — tag `v<version>` from the build,
title the bare version, body the commit subjects since the last published release, cut
from the default branch — pointed at this repository rather than the maintained list, so
it needs no arguments and does not care which directory you run it from.

The version comes from build.gradle.kts, BlobLib being Gradle rather than Maven.

Publishing is public and awkward to walk back, so it runs in --dry-run unless you pass
--yes. It refuses when the branch is not the default one, when commits are unpushed, or
when a release for this version already exists.

Requires the `gh` CLI, authenticated.

Usage:
    python3 tools/github_release_bloblib.py [--yes] [--draft] [--prerelease]
                                            [--no-merges] [--allow-existing] [--dry-run]
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import github_release_maintained_repos as shared  # noqa: E402

BLOBLIB = Path(__file__).resolve().parent.parent


def main() -> int:
    # The shared tool already knows how to do this; it just needs telling which repo,
    # and BlobLib's own root is the one answer this entry point ever wants.
    argv = sys.argv[1:]
    for flag in ("--repo", "--only", "-l", "--list"):
        if any(arg == flag or arg.startswith(f"{flag}=") for arg in argv):
            print(f"{flag} makes no sense here — this releases BlobLib. "
                  f"Use github_release_maintained_repos.py for anything else.",
                  file=sys.stderr)
            return 2

    sys.argv = [sys.argv[0], *argv, "--repo", str(BLOBLIB)]
    return shared.main()


if __name__ == "__main__":
    sys.exit(main())
