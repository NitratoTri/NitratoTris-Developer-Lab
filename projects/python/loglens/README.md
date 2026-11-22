# LogLens

LogLens is a dependency-free command-line tool for inspecting structured application logs in JSON Lines (`.jsonl`) format. It summarizes severity levels and services, ranks repeated errors, and continues when individual records are malformed.

## Why this project?

Backend developers often need a quick first-pass view of application logs. LogLens demonstrates practical Python skills: input validation, streaming file processing, aggregation, a CLI, deterministic output, and automated tests.

## Requirements and installation

- Python 3.11 or newer
- No runtime third-party dependencies

```bash
python -m venv .venv
source .venv/bin/activate  # Windows: .venv\Scripts\activate
python -m pip install -e .
```

## Input format

Each non-empty line must be a JSON object with `timestamp` (ISO 8601 including a timezone), `level`, `message`, and optional `service` (defaults to `unknown`).

```json
{"timestamp":"2026-01-01T10:30:00Z","level":"INFO","service":"orders","message":"Order accepted"}
{"timestamp":"2026-01-01T10:31:12Z","level":"ERROR","service":"payments","message":"Gateway timeout"}
{"timestamp":"2026-01-01T10:31:20Z","level":"ERROR","service":"payments","message":"Gateway timeout"}
```

Blank lines are ignored. Malformed non-empty records are counted and skipped. `ERROR` and `CRITICAL` messages are grouped by exact message text; ties are ordered alphabetically for reproducible reports.

## Usage

```bash
loglens application.jsonl
loglens application.jsonl --top 10
loglens application.jsonl --json
```

The command returns exit code `2` if the file cannot be read or `--top` is negative. Malformed records do not abort analysis; their count remains visible in the report.

## Tests

```bash
python -m unittest discover -s tests -v
```

## Design notes

- Input is consumed one line at a time rather than loaded entirely into memory.
- Runtime dependencies are limited to the Python standard library.
- Deterministic ordering makes reports easier to compare and test.
- This diagnostic helper is not a replacement for centralized logging or alerting.

## Privacy

Analyze logs locally. Never commit production logs or files containing credentials, access tokens, personal data, or other secrets.

## AI assistance

This project was prepared with AI assistance. Review, adapt, and understand the implementation before presenting it in a CV or interview.
