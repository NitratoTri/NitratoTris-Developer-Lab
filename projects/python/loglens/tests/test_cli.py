import io
import json
import tempfile
import unittest
from pathlib import Path

from loglens.cli import main


class CliTests(unittest.TestCase):
    def test_json_output(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "app.jsonl"
            path.write_text(json.dumps({"timestamp": "2026-01-01T10:00:00Z", "level": "ERROR", "message": "gateway timeout"}) + "\n", encoding="utf-8")
            output, errors = io.StringIO(), io.StringIO()
            code = main([str(path), "--json"], output, errors)
        payload = json.loads(output.getvalue())
        self.assertEqual(code, 0)
        self.assertEqual(payload["valid_records"], 1)
        self.assertEqual(payload["services"], {"unknown": 1})
        self.assertEqual(errors.getvalue(), "")

    def test_missing_file_returns_two(self):
        output, errors = io.StringIO(), io.StringIO()
        code = main(["/missing/loglens.jsonl"], output, errors)
        self.assertEqual(code, 2)
        self.assertIn("cannot read", errors.getvalue())

    def test_negative_top_returns_two(self):
        output, errors = io.StringIO(), io.StringIO()
        code = main(["unused.jsonl", "--top", "-1"], output, errors)
        self.assertEqual(code, 2)
        self.assertIn("--top must be zero or greater", errors.getvalue())


if __name__ == "__main__":
    unittest.main()
