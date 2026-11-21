"""Top K Frequent Elements.

Time: O(n + u log u), where n is the input length and u is the number
of distinct values. Sorting values within frequency buckets makes ties
deterministic.
Space: O(n + u) for the frequency buckets and counter.
"""

from collections import Counter


def top_k_frequent(nums: list[int], k: int) -> list[int]:
    """Return the k most frequent values, breaking ties by ascending value.

    Raises:
        ValueError: if k is outside [0, number of distinct values].
    """
    counts = Counter(nums)
    distinct_count = len(counts)
    if not 0 <= k <= distinct_count:
        raise ValueError("k must be between 0 and the number of distinct values")
    if k == 0:
        return []

    buckets: list[list[int]] = [[] for _ in range(len(nums) + 1)]
    for value, frequency in counts.items():
        buckets[frequency].append(value)

    result: list[int] = []
    for frequency in range(len(buckets) - 1, 0, -1):
        for value in sorted(buckets[frequency]):
            result.append(value)
            if len(result) == k:
                return result

    return result


if __name__ == "__main__":
    sample = [1, 1, 1, 2, 2, 3]
    print(top_k_frequent(sample, 2))  # [1, 2]
