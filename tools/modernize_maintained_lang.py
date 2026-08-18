#!/usr/bin/env python3
"""Migrate legacy BlobMessage lang YAML to ModernMessage format, across every
repo listed in bloblib-memory/maintained repos.md.

Reuses the exact line-based rewrite from modernize_blob_messages.py: comments,
quoting style, key order and line wrapping in untouched parts of the file are
preserved exactly. See that script's docstring for the format being migrated.

Each repo is expected to keep its lang file(s) under src/main/resources, matched
recursively as *_lang.yml / *_lang.yaml -- this also picks up locale overlays
such as resources/es_es/foo_lang.yml (see TranslatableArea's locale-overlay
support) alongside the default resources/foo_lang.yml.
(BlobLib's own bloblib_lang.yml included, since it's listed via --include-self.)

Usage:
    python3 tools/modernize_maintained_lang.py [-l LIST] [--only NAME ...]
                                                [--dry-run] [--no-backup]
                                                [--include-self]
"""

from __future__ import annotations

import argparse
import os
import shutil
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from modernize_blob_messages import migrate_text  # noqa: E402

ROOT = Path(__file__).resolve().parent.parent
DEFAULT_LIST = ROOT / "bloblib-memory" / "maintained repos.md"


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


def lang_files(repo: Path):
    resources = repo / "src" / "main" / "resources"
    if not resources.is_dir():
        return
    yield from sorted(resources.rglob("*_lang.yml"))
    yield from sorted(resources.rglob("*_lang.yaml"))


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Migrate legacy BlobMessage lang YAML to ModernMessage format across maintained repos."
    )
    parser.add_argument("-l", "--list", type=Path, default=DEFAULT_LIST,
                         help="path to maintained repos.md (default: bloblib-memory/maintained repos.md)")
    parser.add_argument("--only", nargs="*", default=None,
                         help="restrict to repos whose directory name matches one of these (case-insensitive)")
    parser.add_argument("--include-self", action="store_true",
                         help="also migrate BlobLib's own src/main/resources/bloblib_lang.yml")
    parser.add_argument("--dry-run", action="store_true", help="report changes without writing")
    parser.add_argument("--no-backup", action="store_true", help="do not write .bak copies")
    args = parser.parse_args()

    if not args.list.is_file():
        print(f"error: not a file: {args.list}", file=sys.stderr)
        return 2

    repos = parse_list(args.list)
    if args.include_self:
        repos.insert(0, ROOT)

    if args.only:
        wanted = {name.lower() for name in args.only}
        repos = [r for r in repos if r.name.lower() in wanted]

    warnings: list[str] = []
    files_changed = 0
    messages_converted = 0

    for repo in repos:
        if not repo.is_dir():
            warnings.append(f"{repo}: not a directory, skipped")
            continue
        for yml in lang_files(repo):
            original = yml.read_text(encoding="utf-8")
            migrated, count = migrate_text(original, yml, warnings.append)
            if count == 0 or migrated == original:
                continue
            files_changed += 1
            messages_converted += count
            print(f"{'[dry-run] ' if args.dry_run else ''}{yml}: {count} message(s)")
            if args.dry_run:
                continue
            if not args.no_backup:
                shutil.copy2(yml, yml.with_suffix(yml.suffix + ".bak"))
            yml.write_text(migrated, encoding="utf-8")

    for warning in warnings:
        print(f"warning: {warning}", file=sys.stderr)

    print(f"\n{messages_converted} message(s) across {files_changed} file(s)"
          f"{' would be' if args.dry_run else ''} migrated.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
