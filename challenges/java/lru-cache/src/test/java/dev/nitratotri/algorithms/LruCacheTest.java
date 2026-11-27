package dev.nitratotri.algorithms;

/** Lightweight test harness that runs with the JDK only (no external library). */
public final class LruCacheTest {
    private static int passed;

    private LruCacheTest() {
    }

    public static void main(String[] args) {
        run("standard LRU sequence", LruCacheTest::standardSequence);
        run("get promotes an entry", LruCacheTest::getPromotesEntry);
        run("updating a key promotes it", LruCacheTest::updatePromotesEntry);
        run("least recently used entry is evicted", LruCacheTest::evictsLeastRecent);
        run("capacity one", LruCacheTest::capacityOne);
        run("zero capacity stores nothing", LruCacheTest::zeroCapacity);
        run("negative capacity is rejected", LruCacheTest::negativeCapacity);
        run("negative keys and values are supported", LruCacheTest::negativeKeysAndValues);
        run("updating does not change size", LruCacheTest::updateDoesNotChangeSize);
        run("missing lookups do not change recency", LruCacheTest::missingLookupDoesNotChangeRecency);
        System.out.println("All " + passed + " test groups passed.");
    }

    private static void standardSequence() {
        LruCache cache = new LruCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        assertEquals(1, cache.get(1));
        cache.put(3, 3);
        assertEquals(-1, cache.get(2));
        cache.put(4, 4);
        assertEquals(-1, cache.get(1));
        assertEquals(3, cache.get(3));
        assertEquals(4, cache.get(4));
    }

    private static void getPromotesEntry() {
        LruCache cache = new LruCache(2);
        cache.put(10, 100);
        cache.put(20, 200);
        assertEquals(100, cache.get(10));
        cache.put(30, 300);
        assertEquals(-1, cache.get(20));
        assertEquals(100, cache.get(10));
    }

    private static void updatePromotesEntry() {
        LruCache cache = new LruCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(1, 11);
        cache.put(3, 30);
        assertEquals(11, cache.get(1));
        assertEquals(-1, cache.get(2));
    }

    private static void evictsLeastRecent() {
        LruCache cache = new LruCache(3);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        cache.get(1);
        cache.get(2);
        cache.put(4, 40);
        assertEquals(-1, cache.get(3));
        assertEquals(3, cache.size());
    }

    private static void capacityOne() {
        LruCache cache = new LruCache(1);
        cache.put(1, 10);
        cache.put(2, 20);
        assertEquals(-1, cache.get(1));
        assertEquals(20, cache.get(2));
        assertEquals(1, cache.size());
    }

    private static void zeroCapacity() {
        LruCache cache = new LruCache(0);
        cache.put(1, 10);
        assertEquals(-1, cache.get(1));
        assertEquals(0, cache.size());
    }

    private static void negativeCapacity() {
        expectIllegalArgument(() -> new LruCache(-1));
    }

    private static void negativeKeysAndValues() {
        LruCache cache = new LruCache(2);
        cache.put(-7, -70);
        cache.put(0, -5);
        assertEquals(-70, cache.get(-7));
        assertEquals(-5, cache.get(0));
    }

    private static void updateDoesNotChangeSize() {
        LruCache cache = new LruCache(2);
        cache.put(5, 50);
        cache.put(5, 55);
        assertEquals(1, cache.size());
        assertEquals(55, cache.get(5));
    }

    private static void missingLookupDoesNotChangeRecency() {
        LruCache cache = new LruCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        assertEquals(-1, cache.get(99));
        cache.put(3, 30);
        assertEquals(-1, cache.get(1));
        assertEquals(20, cache.get(2));
    }

    private static void run(String name, Runnable test) {
        test.run();
        passed++;
        System.out.println("PASS: " + name);
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void expectIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }
}
