package dev.nitratotri.algorithms;

import java.util.Arrays;

/** Lightweight tests that run with the JDK only (no external test library). */
public final class MergeIntervalsTest {
    private static int passed;

    private MergeIntervalsTest() {
    }

    public static void main(String[] args) {
        run("standard example", MergeIntervalsTest::standardExample);
        run("unsorted input", MergeIntervalsTest::unsortedInput);
        run("nested intervals", MergeIntervalsTest::nestedIntervals);
        run("touching endpoints", MergeIntervalsTest::touchingEndpoints);
        run("empty and singleton inputs", MergeIntervalsTest::emptyAndSingleton);
        run("negative and integer boundary values", MergeIntervalsTest::boundaryValues);
        run("input remains unchanged", MergeIntervalsTest::inputRemainsUnchanged);
        run("invalid input is rejected", MergeIntervalsTest::invalidInputIsRejected);
        System.out.println("All " + passed + " test groups passed.");
    }

    private static void standardExample() {
        assertIntervals(
                new int[][] {{1, 6}, {8, 10}, {15, 18}},
                MergeIntervalsmerge(new int[][] {{1, 3}, {2, 6}, {8, 10}, {15, 18}}));
    }

    private static void unsortedInput() {
        assertIntervals(
                new int[][] {{1, 3}, {7, 9}},
                MergeIntervals.merge(new int[][] {{7, 9}, {2, 3}, {1, 2}, {8, 8}}));
    }

    private static void nestedIntervals() {
        assertIntervals(
                new int[][] {{1, 10}},
                MergeIntervals.merge(new int[][] {{1, 10}, {2, 3}, {4, 8}}));
    }

    private static void touchingEndpoints() {
        assertIntervals(
                new int[][] {{1, 7}},
                MergeIntervals.merge(new int[][] {{1, 3}, {3, 5}, {5, 7}}));
    }

    private static void emptyAndSingleton() {
        assertIntervals(new int[0][], MergeIntervals.merge(new int[0][]));
        assertIntervals(
                new int[][] {{-2, 4}},
                MergeIntervals.merge(new int[][] {{-2, 4}}));
    }

    private static void boundaryValues() {
        assertIntervals(
                new int[][] {
                    {Integer.MIN_VALUE, Integer.MIN_VALUE},
                    {-5, 0},
                    {Integer.MAX_VALUE, Integer.MAX_VALUE}
                },
                MergeIntervals.merge(new int[][] {
                    {Integer.MAX_VALUE, Integer.MAX_VALUE},
                    {-5, -1},
                    {Integer.MIN_VALUE, Integer.MIN_VALUE},
                    {-2, 0}
                }));
    }

    private static void inputRemainsUnchanged() {
        int[][] input = {{5, 7}, {1, 3}, {2, 4}};
        int[][] original = deepCopy(input);
        MergeIntervals.merge(input);
        assertIntervals(original, input);
    }

    private static void invalidInputIsRejected() {
        expectIllegalArgument(() -> MergeIntervals.merge(null));
        expectIllegalArgument(() -> MergeIntervals.merge(new int[][] {null}));
        expectIllegalArgument(() -> MergeIntervals.merge(new int[][] {{1}}));
        expectIllegalArgument(() -> MergeIntervals.merge(new int[][] {{1, 2, 3}}));
        expectIllegalArgument(() -> MergeIntervals.merge(new int[][] {{4, 2}}));
    }

    private static void run(String name, Runnable test) {
        test.run();
        passed++;
        System.out.println("PASS: " + name);
    }

    private static void assertIntervals(int[][] expected, int[][] actual) {
        if (!Arrays.deepEquals(expected, actual)) {
            throw new AssertionError("Expected " + Arrays.deepToString(expected)
                    + " but got " + Arrays.deepToString(actual));
        }
    }

    private static int[][] deepCopy(int[][] source) {
        return Arrays.stream(source).map(int[]::clone).toArray(int[][]::new);
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
