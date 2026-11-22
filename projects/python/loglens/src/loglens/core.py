"""Streaming analysis of structured JSON Lines application logs."""
from __future__ import annotations

import json
from collections import Counter
from dataclasses import asdict, dataclass
from datetime import datetime
from typing import Iterable


@dataclass(frozen=True, slots=True)
class AnalysisSummary:
    lines_read: int
    blank_lines: int
    valid_records: int
    malformed_lines: int
    levels: dict[str, int]
    services: dict[str, int]
    top_errors: list[dict[str, object]]

    def to_dict(self) -> dict[str, object]:
        """Return a JSON-serializable summary."""
        return asdict(self)


def _validate_record(raw: str) -> tuple[str, str, str]:
    item = json.loads(raw)
    if not isinstance(item, dict):
        raise ValueError("record must be a JSON object")
    timestamp, level, message = item.get("timestamp"), item.get("level"), item.get("message")
    service = item.get("service", "unknown")
    if not all(isinstance(value, str) and value.strip() for value in (timestamp, level, message, service)):
        raise ValueError("timestamp, level, message and service must be non-empty strings")
    normalized = timestamp.strip()
    if normalized.endswith("Z"):
        normalized = normalized[:-1] + "+00:00"
    parsed = datetime.fromisoformat(normalized)
    if parsed.utcoffset() is None:
        raise ValueError("timestamp must include a timezone")
    return level.strip().upper(), message.strip(), service.strip()


def analyze_lines(lines: Iterable[str], top: int = 5) -> AnalysisSummary:
    """Aggregate JSONL records; skip blank lines and count malformed records."""
    if top < 0:
        raise ValueError("top must be zero or greater")
    levels: Counter[str] = Counter()
    services: Counter[str] = Counter()
    errors: Counter[str] = Counter()
    lines_read = blank = valid = malformed = 0

    for raw in lines:
        lines_read += 1
        if not raw.strip():
            blank += 1
            continue
        try:
            level, message, service = _validate_record(raw)
        except (json.JSONDecodeError, ValueError):
            malformed += 1
            continue
        valid += 1
        levels[level] += 1
        services[service] += 1
        if level in {"ERROR", "CRITICAL"}:
            errors[message] += 1

    ranked = [
        {"message": message, "count": count}
        for message, count in sorted(errors.items(), key=lambda pair: (-pair[1], pair[0]))[:top]
    ]
    return AnalysisSummary(
        lines_read, blank, valid, malformed,
        dict(sorted(levels.items())), dict(sorted(services.items())), ranked
    )
