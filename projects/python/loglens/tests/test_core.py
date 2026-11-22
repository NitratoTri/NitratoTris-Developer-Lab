import json
import unittest

from loglens.core import analyze_lines


def row(level="INFO", message="ready", service="api", timestamp="2026-01-01T10:00:00Z"):
    return json.dumps({"timestamp": timestamp, "level": level, "message": message, "service": service})


class AnalyzeLinesTests(unittest.TestCase):
    def test_aggregates_levels_services_and_repeated_errors(self):
        result = analyze_lines([row(), row("ERROR", "timeout", "payments"), row("CRITICAL", "timeout", "payments")])
        self.assertEqual(result.valid_records, 3)
        self.assertEqual(result.levels, {"CRITICAL": 1, "ERROR": 1, "INFO": 1})
        self.assertEqual(result.services, {"api": 1, "payments": 2})
        self.assertEqual(result.top_errors, [{"message": "timeout", "count": 2}])

    def test_blank_and_malformed_lines_are_separate(self):
        result = analyze_lines(["\n", "not-json", row()])
        self.assertEqual((result.lines_read, result.blank_lines, result.malformed_lines, result.valid_records), (3, 1, 1, 1))

    def test_timezone_is_required(self):
        result = analyze_lines([row(timestamp="2026-01-01T10:00:00")])
        self.assertEqual(result.malformed_lines, 1)

    def test_non_object_json_is_malformed(self):
        self.assertEqual(analyze_lines(["[]"]).malformed_lines, 1)

    def test_missing_service_defaults_to_unknown(self):
        data = json.dumps({"timestamp": "2026-01-01T10:00:00+00:00", "level": "info", "message": "ok"})
        self.assertEqual(analyze_lines([data]).services, {"unknown": 1})

    def test_ties_are_alphabetical_and_top_limits_results(self):
        result = analyze_lines([row("ERROR", "zeta"), row("ERROR", "alpha"), row("ERROR", "zeta"), row("ERROR", "alpha")], top=1)
        self.assertEqual(result.top_errors, [{"message": "alpha", "count": 2}])

    def test_zero_top_is_allowed_and_negative_top_is_rejected(self):
        self.assertEqual(analyze_lines([row("ERROR")], top=0).top_errors, [])
        with self.assertRaises(ValueError):
            analyze_lines([], top=-1)


if __name__ == "__main__":
    unittest.main()
