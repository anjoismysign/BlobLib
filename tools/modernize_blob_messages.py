#!/usr/bin/env python3
"""
Migrate legacy BlobMessage YAML files to the ModernMessage format.

Legacy format required a discriminator field ``Type: X`` and named the payload
field differently depending on that type (``Message`` for CHAT/ACTIONBAR).
ModernMessage drops the discriminator entirely and uses one standardized field
name per channel: Chat, Hover, Actionbar, Title, Subtitle, FadeIn, Stay,
FadeOut, BlobSound.

Usage:
    python3 modernize_blob_messages.py <plugins-directory> [--dry-run] [--no-backup]

For every immediate child directory of <plugins-directory> (non-recursive),
the script looks for a ``BlobMessage`` directory and rewrites every .yml/.yaml
file found under it.

The rewrite is line-based so comments, quoting style, key order and line
wrapping in untouched parts of the file are preserved exactly.
"""

from __future__ import annotations

import argparse
import re
import shutil
import sys
from pathlib import Path

# Legacy "Type" -> how to rename the type-specific "Message" key.
# Types not listed here already use ModernMessage field names; they only need
# the "Type" line removed.
MESSAGE_KEY_BY_TYPE = {
    "CHAT": "Chat",
    "ACTIONBAR": "Actionbar",
}

KNOWN_TYPES = {
    "CHAT",
    "ACTIONBAR",
    "TITLE",
    "ACTIONBAR_TITLE",
    "CHAT_ACTIONBAR",
    "CHAT_TITLE",
    "CHAT_ACTIONBAR_TITLE",
}

TYPE_LINE = re.compile(
    r"^(?P<indent>[ ]*)(?P<kq>['\"]?)Type(?P=kq):[ ]*"
    r"(?P<quote>['\"]?)(?P<type>[A-Za-z_]+)(?P=quote)[ ]*(?P<comment>#.*)?$"
)
MESSAGE_LINE = re.compile(r"^(?P<indent>[ ]*)(?P<quote>['\"]?)Message(?P=quote):(?P<rest>.*)$")


def indent_of(line: str) -> int:
    return len(line) - len(line.lstrip(" "))


def is_blank(line: str) -> bool:
    return line.strip() == "" or line.lstrip().startswith("#")


def sibling_range(lines: list[str], index: int, indent: int) -> tuple[int, int]:
    """Bounds [start, end) of the mapping block that owns lines[index].

    A sibling key sits at exactly `indent`; continuation/child lines sit
    deeper. The block ends at the first content line shallower than `indent`.
    """
    start = index
    while start > 0:
        prev = lines[start - 1]
        if is_blank(prev):
            start -= 1
            continue
        if indent_of(prev) < indent:
            break
        start -= 1

    end = index + 1
    while end < len(lines):
        cur = lines[end]
        if is_blank(cur):
            end += 1
            continue
        if indent_of(cur) < indent:
            break
        end += 1

    return start, end


def migrate_text(text: str, path: Path, warn) -> tuple[str, int]:
    lines = text.splitlines(keepends=True)
    drop: set[int] = set()
    renames: dict[int, str] = {}
    converted = 0

    for i, line in enumerate(lines):
        match = TYPE_LINE.match(line.rstrip("\r\n"))
        if not match:
            continue
        message_type = match.group("type")
        if message_type not in KNOWN_TYPES:
            warn(f"{path}:{i + 1}: unknown message type {message_type!r}, left untouched")
            continue

        indent = len(match.group("indent"))
        # The discriminator goes away and so does any comment annotating it:
        # the trailing comment on the line itself, plus the contiguous run of
        # comment lines directly above it. A blank line breaks that attachment.
        drop.add(i)
        j = i - 1
        while j >= 0 and lines[j].lstrip().startswith("#"):
            drop.add(j)
            j -= 1
        converted += 1

        new_key = MESSAGE_KEY_BY_TYPE.get(message_type)
        if new_key is None:
            continue

        start, end = sibling_range(lines, i, indent)
        for j in range(start, end):
            if j == i:
                continue
            sibling = MESSAGE_LINE.match(lines[j].rstrip("\r\n"))
            if sibling and len(sibling.group("indent")) == indent:
                renames[j] = f"{' ' * indent}{new_key}:{sibling.group('rest')}"
                break
        else:
            warn(f"{path}:{i + 1}: {message_type} message has no 'Message' field")

    if converted == 0:
        return text, 0

    out: list[str] = []
    for i, line in enumerate(lines):
        if i in drop:
            continue
        if i in renames:
            newline = line[len(line.rstrip("\r\n")):]
            out.append(renames[i] + newline)
            continue
        out.append(line)
    return "".join(out), converted


def blob_message_dirs(plugins_dir: Path):
    for child in sorted(plugins_dir.iterdir()):
        if not child.is_dir():
            continue
        candidate = child / "BlobMessage"
        if candidate.is_dir():
            yield candidate


def main() -> int:
    parser = argparse.ArgumentParser(description="Migrate legacy BlobMessage YAML to ModernMessage format.")
    parser.add_argument("plugins_directory", type=Path, help="server 'plugins' directory")
    parser.add_argument("--dry-run", action="store_true", help="report changes without writing")
    parser.add_argument("--no-backup", action="store_true", help="do not write .bak copies")
    args = parser.parse_args()

    plugins_dir: Path = args.plugins_directory
    if not plugins_dir.is_dir():
        print(f"error: not a directory: {plugins_dir}", file=sys.stderr)
        return 2

    warnings: list[str] = []
    files_changed = 0
    messages_converted = 0

    for message_dir in blob_message_dirs(plugins_dir):
        for yml in sorted(p for p in message_dir.rglob("*") if p.suffix in (".yml", ".yaml") and p.is_file()):
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
