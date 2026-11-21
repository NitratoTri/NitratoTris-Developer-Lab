import unittest

from top_k_frequent_elements import top_k_frequent


class TopKFrequentElementsTests(unittest.TestCase):
    def test_standard_example(self):
        self.assertEqual(top_k_frequent([1, 1, 1, 2, 2, 3], 2), [1, 2])

    def test_single_distinct_value(self):
        self.assertEqual(top_k_frequent([7, 7, 7], 1), [7])

    def test_ties_are_broken_by_ascending_value(self):
        self.assertEqual(top_k_frequent([5, 5, 1, 1, 3], 2), [1, 5])

    def test_negative_values(self):
        self.assertEqual(top_k_frequent([-1, -1, -2, -2, -2], 1), [-2])

    def test_zero_k(self):
        self.assertEqual(top_k_frequent([1, 2, 2], 0), [])

    def test_k_equal_to_distinct_count(self):
        self.assertEqual(top_k_frequent([3, 3, 1, 2], 3), [3, 1, 2])

    def test_empty_input_with_zero_k(self):
        self.assertEqual(top_k_frequent([], 0), [])

    def test_k_greater_than_distinct_count(self):
        with self.assertRaises(ValueError):
            top_k_frequent([1, 2], 3)

    def test_negative_k(self):
        with self.assertRaises(ValueError):
            top_k_frequent([1, 2], -1)


if __name__ == "__main__":
    unittest.main()
