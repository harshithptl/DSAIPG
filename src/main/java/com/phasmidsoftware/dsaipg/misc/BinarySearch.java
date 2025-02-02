/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc;

import java.util.Arrays;

/**
 * Class to provide the functionality of binary search.
 * NOTE that it is essentially the same as the Arrays.binarySearch method.
 */
public class BinarySearch {
    /**
     * Main program
     *
     * @param args Command-line args.
     */
    public static void main(String[] args) {
        int[] ar = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int res1 = binarySearch(ar, 0, ar.length - 1, 3);
        System.out.println(res1);
        int res2 = Arrays.binarySearch(ar, 0, ar.length, 3);
        System.out.println(res2);
    }

    /**
     * Method to do binary search.
     *
     * @param a    the ordered array.
     * @param from the first index on interest.
     * @param to   the first subsequent index that is NOT of interest.
     * @param key  the value we are searching for.
     * @return the index of the element whose value is <code>key</code>, or null if there is no such element.
     */
    public static int binarySearch(int[] a, int from, int to, int key) {
        int low = from;
        int high = to - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (a[mid] < key) {
                low = mid + 1;
            } else if (a[mid] > key) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }
}