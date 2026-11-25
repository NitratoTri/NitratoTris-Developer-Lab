# Longest Substring Without Repeating Characters — TypeScript

**Difficulty:** Medium  
**Topics:** Strings, hash maps, sliding window

## Problem

Given a string `s`, return the length of the longest contiguous substring that contains no repeated characters.

A substring must be contiguous; a subsequence does not have to be.

## Examples

| Input | Output | Explanation |
|---|---:|---|
| `"abcabcbb"` | `3` | `"abc"` is one longest valid substring |
| `"bbbbb"` | `1` | Every character is the same |
| `"pwwkew"` | `3` | `"wke"` is valid; `"pwke"` is not contiguous |
| `""` | `0` | An empty string has no characters |
| `"😀a😀b"` | `3` | `"a😀b"` contains three Unicode code points |

## Contract

- Comparison is case-sensitive.
- Spaces and punctuation are ordinary characters.
- The result counts Unicode code points, not UTF-16 code units or user-perceived grapheme clusters.
- No Unicode normalization is performed. For example, a precomposed accented letter and the same letter followed by a combining mark remain different sequences.

## Approach: sliding window

Maintain a window `[windowStart, currentIndex]` with no repeated code points and a map containing the most recent index of each code point.

For each character:

1. Look up its previous index.
2. If that index is inside the current window, move the left boundary to one position after it.
3. Store the character's current index.
4. Update the maximum window length.

The boundary only moves forward. This is important when a character repeats at an index that is already outside the current window; moving backwards would incorrectly reintroduce characters.

The implementation uses JavaScript's `for...of` iteration, which visits Unicode code points rather than individual UTF-16 surrogate code units.

## Complexity

Let `n` be the number of Unicode code points in the input and `u` the number of distinct code points encountered.

- **Time:** `O(n)` expected. Each code point is visited once, and map operations are expected O(1).
- **Space:** `O(min(n, Σ))`, where Σ is the size of the possible character alphabet; the map stores at most one last-seen index per distinct code point.

## Run locally

Requirements: Node.js 22.6 or later and npm.

```bash
npm install
npm run check
```

Individual checks:

```bash
npm run typecheck
npm test
```

The tests use Node's built-in test runner. Node's built-in TypeScript type stripping is enabled with `--experimental-strip-types`; TypeScript is a development dependency used for static type checking.

## AI assistance

This contribution was prepared with AI assistance. Review the sliding-window invariant, Unicode behavior, and tests before presenting it as a portfolio example.
