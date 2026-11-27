package dev.nitratotri.algorithms;

import java.util.HashMap;
import java.util.Map;

/**
 * A fixed-capacity least-recently-used cache with expected O(1) operations.
 *
 * <p>Both a successful {@link #get(int)} and every {@link #put(int, int)}
 * mark the affected key as most recently used. The cache is not thread-safe.</p>
 */
public final class LruCache {
    private final int capacity;
    private final Map<Integer, Node> entries;
    private final Node mostRecent;
    private final Node leastRecent;

    /**
     * Creates a cache with the supplied maximum number of entries.
     * A zero-capacity cache accepts writes but never stores values.
     *
     * @param capacity maximum number of entries; must not be negative
     * @throws IllegalArgumentException if capacity is negative
     */
    public LruCache(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must not be negative");
        }

        this.capacity = capacity;
        this.entries = new HashMap<>();
        this.mostRecent = new Node(0, 0);
        this.leastRecent = new Node(0, 0);
        mostRecent.next = leastRecent;
        leastRecent.previous = mostRecent;
    }

    /**
     * Returns the value for {@code key} and marks it as most recently used.
     * Returns -1 when the key is absent.
     *
     * @param key key to look up
     * @return stored value, or -1 when absent
     */
    public int get(int key) {
        Node node = entries.get(key);
        if (node == null) {
            return -1;
        }

        moveToMostRecent(node);
        return node.value;
    }

    /**
     * Inserts or updates a value and marks its key as most recently used.
     * If the cache is full, the least recently used entry is evicted.
     * A zero-capacity cache remains empty.
     *
     * @param key key to insert or update
     * @param value value associated with the key
     */
    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }

        Node existing = entries.get(key);
        if (existing != null) {
            existing.value = value;
            moveToMostRecent(existing);
            return;
        }

        Node inserted = new Node(key, value);
        entries.put(key, inserted);
        addAfterMostRecent(inserted);

        if (entries.size() > capacity) {
            Node evicted = leastRecent.previous;
            remove(evicted);
            entries.remove(evicted.key);
        }
    }

    /** Returns the current number of cached entries. */
    public int size() {
        return entries.size();
    }

    private void moveToMostRecent(Node node) {
        remove(node);
        addAfterMostRecent(node);
    }

    private void addAfterMostRecent(Node node) {
        node.previous = mostRecent;
        node.next = mostRecent.next;
        mostRecent.next.previous = node;
        mostRecent.next = node;
    }

    private static void remove(Node node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
    }

    private static final class Node {
        private final int key;
        private int value;
        private Node previous;
        private Node next;

        private Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
