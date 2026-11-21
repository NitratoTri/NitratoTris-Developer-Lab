import unittest

from valid_anagram import is_anagram


class ValidAnagramTests(unittest.TestCase):
    def test_same_characters_in_different_order(self):
        self.assertTrue(is_anagram("listen", "silent"))

    def test_different_characters(self):
        self.assertFalse(is_anagram("rat", "car"))

    def test_repeated_characters(self):
        self.assertTrue(is_anagram("aabbcc", "ccbbaa"))

    def test_different_lengths(self):
        self.assertFalse(is_anagram("a", "aa"))

    def test_empty_strings(self):
        self.assertTrue(is_anagram("", ""))

    def test_case_is_significant(self):
        self.assertFalse(is_anagram("Anagram", "nagaram"))


if __name__ == "__main__":
    unittest.main()
