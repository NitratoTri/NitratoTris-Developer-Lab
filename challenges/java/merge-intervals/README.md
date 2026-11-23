# Merge Intervals — Java

**Difficulty:** Medium  
**Topics:** Arrays, sorting, greedy algorithms

## Problem

Given an array of intervals where each interval is represented by `[start, end]`, merge every pair of overlapping intervals and return the resulting non-overlapping intervals ordered by their start value.

This implementation also merges intervals that touch at an endpoint: `[1, 3]` and `[3, 5]` become `[1, 5]`.

## Examples

| Input | Output |
|---|---|
| `[[1,3],[2,6],[8,10],[15,18]]` | `[[1,6],[8,10],[15,18]]` |
| `[[1,4],[4,5]]` | `[[1,5]]` |
| `[[7,9],[2,3],[1,2]]` | `[[1,3],[7,9]]` |
| `[]` | `[]` |

## Contract

- Each interval must contain exactly two integers.
- `start` must be less than or equal to `end`.
- A null input, null interval, malformed interval, or reversed interval raises `IllegalArgumentException`.
- The input array is not modified; the method returns a new result.
- Integer boundary values are supported without arithmetic on endpoints.

## Approach

1. Validate each interval and make a defensive copy so sorting cannot mutate the caller's data.
2. Sort copies by start, then by end.
3. Visit intervals from left to right. If the next start is greater than the previous merged end, start a new result interval. Otherwise, extend the previous end to the larger endpoint.

After sorting, each interval only needs to be compared with the last merged interval. This greedy invariant is sufficient because all earlier intervals are already ordered and merged.

## Complexity

For `n` input intervals:

- **Time:** `O(n log n)` for sorting, followed by an `O(n)` merge pass.
- **Auxiliary space:** `O(n)` for defensive copies and the result.

## Run tests (JDK 17+
