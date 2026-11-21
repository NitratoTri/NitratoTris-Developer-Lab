# Top K Frequent Elements

**Difficulty:** Medium  
**Topic:** Arrays, hash maps, bucket sort

## Problem

Given an integer array `nums` and an integer `k`, return the `k` most frequent values.

For deterministic output, this implementation breaks equal-frequency ties by ascending numeric value. The original coding-interview problem may accept any order among tied values.

## Examples

| Input | Output | Explanation |
|---|---|---|
| `nums = [1,1,1,2,2,3], k = 2` | `[1,2]` | 1 appears three times; 2 appears twice |
| `nums = [5,5,1,1,3], k = 2` | `[1,5]` | Both 1 and 5 appear twice; ascending value breaks the tie |
| `nums = [7], k = 1` | `[7]` | Only one distinct value exists |

## Constraints and contract

- The input may contain positive, zero, or negative integers.
- `0 <= k <= number of distinct values`.
- Invalid values of `k` raise `ValueError`.
- An empty input is valid only with `k = 0`.

## Approach

1. Count occurrences with `collections.Counter`.
2. Create frequency buckets. A value that appears `f` times is placed in bucket `f`.
3. Traverse buckets from highest frequency to lowest.
4. Sort values inside each bucket to make tie-breaking deterministic, then stop after collecting `k` values.

## Complexity

Let `n` be the number of input elements and `u` the number of distinct values.

- **Time:** `O(n + u log u)` in the worst case. Counting is expected `O(n)`; sorting tied values across buckets is bounded by `O(u log u)`.
- **Space:** `O(n + u)` for the counter and frequency buckets.

## Run

From the repository root:

```bash
python -m unittest discover -s challenges/python -p "test_*.py" -v
```

The solution uses only the Python standard library.

## AI assistance

This contribution was prepared with AI assistance. Review the implementation, tests, and complexity analysis before using it as a portfolio example.
