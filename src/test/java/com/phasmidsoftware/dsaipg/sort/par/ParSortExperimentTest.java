package com.phasmidsoftware.dsaipg.sort.par;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import org.junit.jupiter.api.Test;
import java.util.*;

public class ParSortExperimentTest {

    @Test
    public void findOptimalCutoff() {
        System.out.printf("%-10s %-10s %-20s%n", "Size", "Cutoff", "ParallelTime (ms)");
        System.out.println("=".repeat(50));

        int[] baseArray = generateRandomArray(14_000_000);
        for (int size = 2_000_000; size <= 15_000_000; size += 2_000_000) {
            int[] array = Arrays.copyOfRange(baseArray, 0, size);

            for (int cutoff = 8000; cutoff <= 1_500_000; cutoff *= 2) {
                ParSort.cutoff = cutoff;
                ParSort.setThreadCount(4);

                Benchmark_Timer<int[]> parallelBenchmark = new Benchmark_Timer<>("Parallel Sort",
                        arr -> ParSort.sort(arr, 0, arr.length));
                double parallelTime = parallelBenchmark.runFromSupplier(array::clone, 10);

                System.out.printf("Size - %-10d Cutoff - %-10d Time - %-20.2f%n", size, cutoff, parallelTime);
            }
            System.out.println("=".repeat(50));
        }
    }


    @Test
    public void testThreadCountEffect() {
        System.out.println("=".repeat(45));
        int size = 10_000_000;

        System.out.printf("%-10s %-10s %-20s%n", "Size", "Threads", "ParallelTime (ms)");

        for (int threads = 2; threads <= 64; threads *= 2) {
            ParSort.setThreadCount(threads);
            ParSort.cutoff = 128000;
            int[] randomArray = generateRandomArray(size);

            Benchmark_Timer<int[]> parallelBenchmark = new Benchmark_Timer<>(
                    "Parallel Sort with " + threads + " threads",
                    arr -> ParSort.sort(arr, 0, arr.length)
            );

            double parallelTime = parallelBenchmark.runFromSupplier(randomArray::clone, 10);
            System.out.printf("%-10d %-10d %-20.2f%n", size, threads, parallelTime);
        }
    }

    @Test
    public void findOptimalCutoffAndThreadCount() {
        int size = 10_000_000;
        int[] baseArray = generateRandomArray(size);

        System.out.printf("%-10s %-10s %-10s %-20s%n", "Size", "Threads", "Cutoff", "Time (ms)");
        System.out.println("=".repeat(55));

        for (int threads = 2; threads <= 16; threads *= 2) {
            ParSort.setThreadCount(threads);

            for (int cutoff = 64000; cutoff <= 256000; cutoff += 8000) {
                ParSort.cutoff = cutoff;

                Benchmark_Timer<int[]> parallelBenchmark = new Benchmark_Timer<>("Parallel Sort",
                        arr -> ParSort.sort(arr, 0, arr.length));
                double parallelTime = parallelBenchmark.runFromSupplier(baseArray::clone, 10);

                System.out.printf("Size - %-10d Threads - %-10d Cutoff - %-10d Time - %-20.2f%n", size, threads, cutoff, parallelTime);
            }
            System.out.println("=".repeat(55));
        }
    }



    private int[] generateRandomArray(int size) {
        Random rand = new Random();
        return rand.ints(size, 0, 15000000).toArray();
    }
}