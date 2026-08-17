#!/usr/bin/env bash
# Creates a new git branch for a version bump and switches to it.
# Fails fast if there are uncommitted changes.

set -euo pipefail

# Resolve the script's own directory so this works regardless of cwd
# (e.g. Finder double-click starts the shell in $HOME).
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$script_dir"

cd "$(git rev-parse --show-toplevel)"

if [[ -n "$(git status --porcelain)" ]]; then
    echo "Error: you have uncommitted changes. Commit or stash them first." >&2
    git status --short
    exit 1
fi

BUILD_FILE="build.gradle.kts"
current_version="$(grep -E '^version = ' "$BUILD_FILE" | sed -E 's/version = "(.*)"/\1/')"
current_branch="$(git branch --show-current)"

echo "Current branch: ${current_branch}"
echo "Current version: ${current_version}"

read -r -p "New version: " new_version

if [[ -z "$new_version" ]]; then
    echo "Error: new version cannot be empty." >&2
    exit 1
fi

branch_name="${new_version}"

git checkout -b "$branch_name"

echo "Created and switched to branch '${branch_name}'."
echo "Remember to update ${BUILD_FILE}'s version to \"${new_version}\" and commit."
