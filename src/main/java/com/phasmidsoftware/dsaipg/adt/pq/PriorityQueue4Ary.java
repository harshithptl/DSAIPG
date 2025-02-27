package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class PriorityQueue4Ary<K> implements Iterable<K> {

    private final boolean max;
    private final int first;
    private final Comparator<K> comparator;
    private final K[] binHeap;
    private int last;
    private final boolean floyd;

    private static final int pq4aryChildren = 4;

    public PriorityQueue4Ary(boolean max, Object[] binHeap, int first, int last, Comparator<K> comparator, boolean floyd) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.last = last;
        //noinspection unchecked
        this.binHeap = (K[]) binHeap;
        this.floyd = floyd;
    }

    public PriorityQueue4Ary(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {
        this(max, new Object[n + first], first, 0, comparator, floyd);
    }

    public PriorityQueue4Ary(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        this(n, 1, max, comparator, floyd);
    }

    public boolean isEmpty() {
        return last == 0;
    }

    public int size() {
        return last;
    }

    /**
     * Inserts an element into the 4‑ary heap.
     * If the heap is already at capacity, it discards the least eligible element.
     */
    public void give(K key) {
        if (last == binHeap.length - first)
            last--; // At capacity: arbitrarily discard the least eligible element.
        binHeap[++last + first - 1] = key;
        swimUp(last + first - 1);
    }

    /**
     * Removes and returns the root element (max or min, depending on the heap type).
     * Uses either Floyd's snake method or the standard sink method.
     */
    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        if (floyd) return doTake(this::snake);
        else return doTake(this::sink);
    }

    private K doTake(Consumer<Integer> f) {
        K result = binHeap[first];
        swap(first, last-- + first - 1);
        f.accept(first);
        binHeap[last + first] = null;
        return result;
    }

    /**
     * Standard sink operation for the 4‑ary heap.
     */
    void sink(int k) {
        doHeapify(k, (a, b) -> !unordered(a, b));
    }

    /**
     * Uses the Floyd snake method: sinks then swims the element up.
     */
    void snake(int k) {
        swimUp(doHeapify(k, (a, b) -> !unordered(a, b)));
    }

    /**
     * Swims the element at index k upward until the heap order is restored.
     */
    void swimUp(int k) {
        int i = k;
        while (i > first && unordered(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Returns true if the elements at indices i and j are out of order.
     */
    boolean unordered(int i, int j) {
        return (comparator.compare(binHeap[i], binHeap[j]) > 0) ^ max;
    }

    /**
     * Adjusts the subtree rooted at index k to maintain the heap property.
     * For a 4‑ary heap, this method examines up to 4 children.
     *
     * @return the final position of the element originally at index k.
     */
    private int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (firstChild(i) <= last + first - 1) {
            int best = firstChild(i);
            int lastChild = Math.min(firstChild(i) + pq4aryChildren - 1, last + first - 1);
            for (int j = firstChild(i) + 1; j <= lastChild; j++) {
                if (unordered(best, j)) {
                    best = j;
                }
            }
            if (p.test(i, best)) break;
            swap(i, best);
            i = best;
        }
        return i;
    }

    /**
     * Exchanges the elements at indices i and j.
     */
    private void swap(int i, int j) {
        K tmp = binHeap[i];
        binHeap[i] = binHeap[j];
        binHeap[j] = tmp;
    }

    /**
     * Computes the parent index for a node at index k in a 4‑ary heap.
     */
    private int parent(int k) {
        return (k - first - 1) / pq4aryChildren + first;
    }

    /**
     * Computes the index of the first child for a node at index k in a 4‑ary heap.
     */
    private int firstChild(int k) {
        return pq4aryChildren * (k - first) + first + 1;
    }

    /**
     * Returns an iterator over the elements of the heap.
     */
    public Iterator<K> iterator() {
        Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(binHeap, last + first)));
        Iterator<K> result = copy.iterator();
        if (first > 0 && result.hasNext()) result.next();
        return result;
    }
}
