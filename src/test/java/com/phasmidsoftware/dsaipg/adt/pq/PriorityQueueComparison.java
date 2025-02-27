package com.phasmidsoftware.dsaipg.adt.pq;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Demonstration of timing inserts+deletes together.
 *
 * We do:
 *  1) Insert n random elements
 *  2) Delete n/4
 *
 * The total time is measured, so that we see the effect of Floyd's trick
 * on the deletion part, but we also include insertion cost in the total.
 */
public class PriorityQueueComparison {

    // Make sure M is large enough so that we never overflow the internal array.
    // E.g., if your largest n=128000, setting M=200000 is safe.
    static final int M = 4096005;

    // Example input sizes:
    private static final int[] SIZES = {
            128000,
            512000,
            1024000,
            2048000,
            4096000
    };

    public static void main(String[] args) {
        System.out.println("Benchmark: time both inserts and deletes together.");
        System.out.println("  We'll do n inserts, then n/4 deletes.");
        System.out.println("  We'll compare with/without Floyd's trick on binary & 4-ary heaps, plus Fibonacci.");

        for (int n : SIZES) {
            System.out.println("\n=================== n = " + n + " ===================");

            double timeBinaryNoFloyd = runBenchmarkBinaryHeap("BinaryHeap-NoFloyd", false, n);
            double timeBinaryFloyd   = runBenchmarkBinaryHeap("BinaryHeap-Floyd",   true,  n);

            double time4AryNoFloyd   = runBenchmark4AryHeap("4AryHeap-NoFloyd", false, n);
            double time4AryFloyd     = runBenchmark4AryHeap("4AryHeap-Floyd",   true,  n);

            double timeFibonacci     = runBenchmarkFibonacci("FibonacciHeap", n);

            System.out.println("-- Average times (ms) over 10 runs --");
            System.out.println("Binary Heap (no Floyd):   " + timeBinaryNoFloyd);
            System.out.println("Binary Heap (Floyd):      " + timeBinaryFloyd);
            System.out.println("4-ary Heap (no Floyd):    " + time4AryNoFloyd);
            System.out.println("4-ary Heap (Floyd):       " + time4AryFloyd);
            System.out.println("Fibonacci Heap:           " + timeFibonacci);
        }
    }

    /**
     * Benchmark: (1) insert n random integers into a binary-heap-based PQ,
     *            (2) delete n/4 times,
     *            measure total time for these combined ops.
     *
     * @param label   A label for printing
     * @param floyd   If true, enable Floyd's trick for the binary heap
     * @param n       Number of inserts, then n/4 deletes
     * @return average time in ms over 10 runs
     */
    private static double runBenchmarkBinaryHeap(String label, boolean floyd, int n) {
        Supplier<PriorityQueue<Integer>> supplier =
                () -> new PriorityQueue<Integer>(M, true, Comparator.naturalOrder(), floyd);

        Benchmark_Timer<PriorityQueue<Integer>> timer = new Benchmark_Timer<>(
                label,
                q -> {
            Random rand = new Random();
            for (int i = 0; i < n; i++) {
                q.give(rand.nextInt());
            }
            for (int i = 0; i < n / 4; i++) {
                try {
                    if (!q.isEmpty()) {
                        q.take();
                    }
                } catch (PQException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        null
        );
        return timer.runFromSupplier(supplier, 10);
    }

    private static double runBenchmark4AryHeap(String label, boolean floyd, int n) {
        Supplier<PriorityQueue4Ary<Integer>> supplier =
                () -> new PriorityQueue4Ary<Integer>(M, true, Comparator.naturalOrder(), floyd);

        Benchmark_Timer<PriorityQueue4Ary<Integer>> timer = new Benchmark_Timer<>(
                label,
                q -> {
                    Random rand = new Random();
                    for (int i = 0; i < n; i++) {
                        q.give(rand.nextInt());
                    }
                    for (int i = 0; i < n / 4; i++) {
                        try {
                            if (!q.isEmpty()) {
                                q.take();
                            }
                        } catch (PQException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                null
        );
        return timer.runFromSupplier(supplier, 10);
    }

    private static double runBenchmarkFibonacci(String label, int n) {
        Supplier<FibonacciHeap<Integer>> supplier =
                () -> new FibonacciHeap<Integer>(true, Comparator.naturalOrder());

        Benchmark_Timer<FibonacciHeap<Integer>> timer = new Benchmark_Timer<>(
                label,
                q -> {
                    Random rand = new Random();
                    for (int i = 0; i < n; i++) {
                        q.give(rand.nextInt());
                    }
                    for (int i = 0; i < n / 4; i++) {
                        try {
                            if (!q.isEmpty()) {
                                q.take();
                            }
                        } catch (PQException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                null
        );
        return timer.runFromSupplier(supplier, 10);
    }
}
