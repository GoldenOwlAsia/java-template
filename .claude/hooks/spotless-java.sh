#!/usr/bin/env bash
# PostToolUse hook: format the edited .java file with Spotless.
# Reads the hook JSON from stdin; no jq dependency.
set -u

input=$(cat)
file=$(printf '%s' "$input" | sed -n 's/.*"file_path"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' | head -n 1)

case "$file" in
  *.java) ;;
  *) exit 0 ;;
esac

cd "${CLAUDE_PROJECT_DIR:-.}" || exit 0

# spotlessFiles is a regex list; escape the path.
pattern=$(printf '%s' "$file" | sed 's/[][\.*^$()+?{}|]/\\&/g')

if ! out=$(./mvnw -q -B spotless:apply -DspotlessFiles="$pattern" 2>&1); then
  echo "spotless:apply failed for $file:" >&2
  # Drop JVM/Maven warning noise; keep only [ERROR] lines.
  errors=$(printf '%s\n' "$out" | grep '^\[ERROR\]' | grep -v '^\[ERROR\][[:space:]]*$')
  printf '%s\n' "${errors:-$(printf '%s\n' "$out" | tail -n 30)}" | head -n 30 >&2
  exit 2
fi
exit 0
