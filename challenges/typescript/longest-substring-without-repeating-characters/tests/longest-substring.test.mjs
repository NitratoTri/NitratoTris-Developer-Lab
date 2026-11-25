import test from 'node:test';
import assert from 'node:assert/strict';
import { lengthOfLongestSubstring } from '../src/longest-substring.ts';

test('standard example with repeated sections', () => {
  assert.equal(lengthOfLongestSubstring('abcabcbb'), 3);
});

test('a single repeated character has length one', () => {
  assert.equal(lengthOfLongestSubstring('bbbbb'), 1);
});

test('does not confuse a subsequence with a substring', () => {
  assert.equal(lengthOfLongestSubstring('pwwkew'), 3);
});

test('returns zero for an empty string', () => {
  assert.equal(lengthOfLongestSubstring(''), 0);
});

test('handles one character', () => {
  assert.equal(lengthOfLongestSubstring('x'), 1);
});

test('handles a string with no repeated characters', () => {
  assert.equal(lengthOfLongestSubstring('abcdef'), 6);
});

test('does not move the window backwards when a stale character repeats', () => {
  assert.equal(lengthOfLongestSubstring('abba'), 2);
});

test('moves the window after the previous occurrence', () => {
  assert.equal(lengthOfLongestSubstring('dvdf'), 3);
});

test('treats spaces and punctuation as characters', () => {
  assert.equal(lengthOfLongestSubstring('a b!a'), 4);
});

test('comparison is case-sensitive', () => {
  assert.equal(lengthOfLongestSubstring('aA'), 2);
});

test('iterates Unicode code points, not UTF-16 code units', () => {
  assert.equal(lengthOfLongestSubstring('😀a😀b'), 3);
});

test('handles repeated supplementary Unicode characters', () => {
  assert.equal(lengthOfLongestSubstring('🍎🍎'), 1);
});

test('handles a longer input', () => {
  assert.equal(
    lengthOfLongestSubstring('abcdefghijklmnopqrstuvwxyz'.repeat(1_000)),
    26
  );
});
