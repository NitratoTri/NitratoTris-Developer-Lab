/**
 * Returns the length, in Unicode code points, of the longest substring
 * that contains no repeated code point.
 *
 * Time: O(n), where n is the number of Unicode code points.
 * Space: O(min(n, alphabet size)) for the last-seen index map.
 */
export function lengthOfLongestSubstring(input: string): number {
  const lastSeenAt = new Map<string, number>();
  let windowStart = 0;
  let currentIndex = 0;
  let maximumLength = 0;

  for (const character of input) {
    const previousIndex = lastSeenAt.get(character);

    if (previousIndex !== undefined && previousIndex >= windowStart) {
      windowStart = previousIndex + 1;
    }

    lastSeenAt.set(character, currentIndex);
    maximumLength = Math.max(
      maximumLength,
      currentIndex - windowStart + 1
    );
    currentIndex += 1;
  }

  return maximumLength;
}
