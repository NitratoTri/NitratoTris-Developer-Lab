"""Command-line interface for LogLens."""
from __future__ import annotations

import argparse
import json
import sys
from pathlib import Path
from typing import Sequence, TextIO

from .core import analyze_lines


def main(argv: Sequence[str] | None = None, stdout: TextIO | None = None, stderr: TextIO | None = None) -> int:
    out = stdout if stdout is not None else sys.stdout
    err = stderr if stderr is not None else sys.stderr
    parser = argparse.ArgumentParser(description="Summarize structured JSON Lines application logs.")
    parser.add_argument("file", type=Path, help="UTF-8 JSONL file to analyze")
    parser.add_argument("--top", type=int, default=5, metavar="N", help="number of frequent errors to show")
    parser.add_argument("--json", action="store_true", dest="json_output", help="emit JSON instead of text")
    args = parser.parse_args(argv)
    if args.top < 0:
        print("loglens: --top must be zero or greater", file=err)
        return 2
    try:
        with args.file.open(encoding="utf-8") as source:
            summary = analyze_lines(source, top=args.top)
    except (OSError, UnicodeError) as exc:
        print(f"loglens: cannot read {args.file}: {exc}", file=err)
        return 2

    if args.json_output:
        print(json.dumps({"source": str(args.file), **summary.to_dict()}, indent=2), file=out)
        return 0

    print(f"LogLens report: {args.file}", file=out)
    print(f"Lines: {summary.lines_read} | valid: {summary.valid_records} | malformed: {summary.malformed_lines} | blank: {summary.blank_lines}", file=out)
    print("\nLevels:", file=out)
    for key, count in summary.levels.items():
        print(f"  {key:<10} {count}", file=out)
    if not summary.levels:
        print("  No valid records", file=out)
    print("\nServices:", file=out)
    for key, count in summary.services.items():
        print(f"  {key:<20} {count}", file=out)
    if not summary.services:
        print("  No valid records", file=out)
    print("\nMost frequent errors:", file=out)
    for item in summary.top_errors:
        print(f"  {item['count']:>4}  {item['message']}", file=out)
    if not summary.top_errors:
        print("  No ERROR or CRITICAL records found", file=out)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
