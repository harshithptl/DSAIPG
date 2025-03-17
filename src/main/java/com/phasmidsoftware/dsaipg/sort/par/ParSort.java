/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * ParSort is a class implementing a parallel sorting algorithm.
 * The sorting is executed using a fork-and-join approach,
 * where large arrays are divided into smaller portions and sorted concurrently.
 * Designed to optimize performance for sorting large integer arrays.
 * This code has been fleshed out by...
 *
 * @author Ziyao Qiao. Thanks very much.
 */
final class ParSort {

    /**
     * Specifies the cutoff value used to determine when to switch from parallel sorting
     * to single-threaded sorting. If the size of the range to be sorted is smaller than
     * this value, {@link Arrays#sort} is used for single-threaded sorting. Otherwise,
     * the range is divided into smaller subarrays, which are sorted in parallel.
     * A larger cutoff value reduces the overhead of thread management but may limit
     * the advantages of parallelism.
     */
    public static int cutoff = 100000;
    private static ForkJoinPool threadPool = new ForkJoinPool();

    public static void setThreadCount(int threads) {
        threadPool = new ForkJoinPool(threads);
    }

    /**
     * Sorts the specified portion of the input array using a parallel sorting algorithm.
     * If the range to be sorted is smaller than a predefined cutoff value, the method
     * utilizes a single-threaded sorting based on {@link Arrays#sort}. For larger ranges,
     * the array is divided into subarrays which are recursively sorted concurrently,
     * and the results are merged into a single sorted array.
     *
     * @param array the array to be sorted
     * @param from  the starting index (inclusive) of the portion of the array to be sorted
     * @param to    the ending index (exclusive) of the portion of the array to be sorted
     */

    public static void sort(int[] array, int from, int to) {
        threadPool.invoke(new SortTask(array, from, to));
    }

    private static class SortTask extends RecursiveAction {
        private final int[] array;
        private final int from, to;

        SortTask(int[] array, int from, int to) {
            this.array = array;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void compute() {
            if (to - from < cutoff) {
                Arrays.sort(array, from, to);
            } else {
                int mid = (from + to) / 2;
                SortTask left = new SortTask(array, from, mid);
                SortTask right = new SortTask(array, mid, to);

                invokeAll(left, right);
                merge(from, mid, to);
            }
        }

        private void merge(int from, int mid, int to) {
            int[] left = Arrays.copyOfRange(array, from, mid);
            int i = 0, j = mid, k = from;

            while (i < left.length && j < to) {
                array[k++] = (left[i] <= array[j]) ? left[i++] : array[j++];
            }

            while (i < left.length) array[k++] = left[i++];
        }
    }
}