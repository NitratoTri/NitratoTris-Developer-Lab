# LRU Cache — Java

**Difficulty:** Medium  
**Topics:** Hash maps, doubly linked lists, design, data structures

## Problem

Design a least-recently-used (LRU) cache with a fixed capacity. Implement:

- \`get(key)\`: return the value when present and mark the key as most recently used; otherwise return \`-1\`.
- \`put(key, value)\`: insert or update a key, mark it as most recently used, and evict the least recently used key when the capacity is exceeded.

Both operations should have expected O(1) time complexity.

## Example

With capacity 2:

| Operation | Result | Cache, most recent first |
|---|---:|---|
| \`put(1, 1)\` | — | 1 |
| \`put(2, 2)\` | — | 2, 1 |
| \`get(1)\` | 1 | 1, 2 |
| \`put(3, 3)\` | — | 3, 1 (key 2 evicted) |
| \`get(2)\` | -1 | 3, 1 |
| \`put(4, 4)\` | — | 4, 3 (key 1 evicted) |
| \`get(1)\` | -1 | 4, 3 |
| \`get(3)\` | 3 | 3, 4 |
| \`get(4)\` | 4 | 4, 3 |

## Contract and edge cases

- Capacity must be zero or greater; a negative capacity throws \`IllegalArgumentException\`.
- A zero-capacity cache never stores entries.
- Updating an existing key replaces its value and makes it most recently used.
- A successful lookup makes the key most recently used; a missing lookup does not change recency.
- Keys and values may be negative. As in the common interview contract, \`-1\` is reserved to signal a missing key, so storing \`-1\` as a value is ambiguous to callers.
- The implementation is not thread-safe.

## Approach

Use two data structures together:

1. A \`HashMap<Integer, Node>\` locates a cache entry by key in expected O(1) time.
2. A doubly linked list stores entries from most recently used to least recently used. Two sentinel nodes simplify insertion and removal at either end.

On \`get\`, look up the node in the map and move it directly after the head sentinel. On \`put\`, update and move an existing node, or insert a new node at the front. If the map exceeds capacity, remove the node immediately before the tail sentinel and delete its key from the map.

The map and list must always contain exactly the same entries. Each list operation changes a constant number of pointers, so no scan is needed to find the eviction candidate.

## Complexity

For each \`get\` or \`put\`:

- **Time:** expected O(1), relying on average-case \`HashMap\` operations and constant-time linked-list updates.
- **Space:** O(capacity) for the map and linked-list nodes.

## Run tests (JDK 17+)

No Maven or external dependencies are required. From this directory:

\`\`\`bash
./run-tests.sh
\`\`\`

The script compiles with Java 17 compatibility, enables all compiler lint warnings as errors, runs the test harness, and cleans its temporary build directory.

## AI assistance

This contribution was prepared with AI assistance. Review the map/list invariant, eviction order, and edge cases; run the tests and be prepared to explain why both data structures are needed before presenting it in a CV or interview.
