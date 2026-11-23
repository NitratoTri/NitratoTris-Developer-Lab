package dev.nitratotri.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Merges overlapping or touching intervals.
 *
 * <p>Time complexity: O(n log n), due to sorting. Space complexity: O(n)
 * for the defensive copies and merged result.</p>
 */
public final class MergeIntervals {
    private MergeIntervals() {
        // Utility class.
    }

    /**
     * Returns a new array containing the merged intervals, ordered by start.
     * Intervals that share an endpoint are merged (for example, [1, 3] and
     * [3, 5] become [1, 5]). The input array and its rows are not modified.
     *
     * @param intervals an array of [start, end] pairs
     * @return merged intervals, or an empty array when the input is empty
     * @throws IllegalArgumentException if the input or one of its intervals
     *                                  is null, malformed, or has start > end
     */
    public static int[][] merge(int[][] intervals) {
        if (intervals == null) {
            throw new IllegalArgumentException("intervals must not be null");
        }

        List<int[]> sorted = new ArrayList<>(intervals.length);
        for (int index = 0; index < intervals.length; index++) {
            int[] interval = intervals[index];
            if (interval == null || interval.length != 2) {
                throw new IllegalArgumentException(
                        "interval at index " + index + " must contain exactly two values");
            }
            if (interval[0] > interval[1]) {
                throw new IllegalArgumentException(
                        "interval at index " + index + " has start greater than end");
            }
            sorted.add(interval.clone());
        }

        sorted.sort(Comparator
                .comparingInt((int[] interval) -> interval[0])
                .thenComparingInt(interval -> interval[1]));

        List<int[]> merged = new ArrayList<>();
        for (int[] current : sorted) {
            if (merged.isEmpty()
                    || current[0] > merged.get(merged.size() - 1)[1]) {
                merged.add(current);
                continue;
            }

            int[] previous = merged.get(merged.size() - 1);
            previous[1] = Math.max(previous[1], current[1]);
        }

        return merged.toArray(new int[merged.size()][]);
    }
}
