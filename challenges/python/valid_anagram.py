"""Valid Anagram — determine whether two strings contain the same characters.

Time complexity: O(n) expected, where n is the combined input length.
Space complexity: O(k), where k is the number of distinct characters.
"""


def is_anagram(first: str, second: str) -> bool:
    """Return True when both strings contain the same characters and counts."""
    if len(first) != len(second):
        return False

    frequencies: dict[str, int] = {}

    for character in first:
        frequencies[character] = frequencies.get(character, 0) + 1

    for character in second:
        if character not in frequencies:
            return False

        frequencies[character] -= 1
        if frequencies[character] == 0:
            del frequencies[character]

    return not frequencies


if __name__ == "__main__":
    examples = [
        ("listen", "silent", True),
        ("rat", "car", False),
        ("aabb", "bbaa", True),
        ("a", "aa", False),
        ("", "", True),
    ]

    for left, right, expected in examples:
        actual = is_anagram(left, right)
        print(f"{left!r}, {right!r} -> {actual} (expected: {expected})")
