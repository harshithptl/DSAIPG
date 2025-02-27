package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;

/**
 * Fibonacci Heap implementation of a Priority Queue.
 *
 * This implementation supports both max and min modes (via the provided comparator).
 * It exposes only the parameters needed for Fibonacci Heap construction.
 *
 * For a max‑heap, the comparator is internally reversed so that the extracted element
 * (via take()) is the maximum.
 *
 * @param <K> the type of key.
 */
public class FibonacciHeap<K> implements Iterable<K> {

    /**
     * Inner class representing a node in the Fibonacci Heap.
     */
    private static class Node<K> {
        K key;
        int degree = 0;
        Node<K> parent = null;
        Node<K> child = null;
        Node<K> left;
        Node<K> right;
        boolean mark = false;

        Node(K key) {
            this.key = key;
            left = this;
            right = this;
        }
    }

    private Node<K> min;          // Pointer to the minimum node (or maximum if max=true).
    private int n;                // Total number of nodes in the heap.
    private final Comparator<K> comparator;
    private final Comparator<K> heapComparator;  // Inverted if max==true.
    private final boolean max;

    /**
     * Primary constructor.
     *
     * @param max        true for a maximum priority queue, false for a minimum priority queue.
     * @param comparator the comparator for the keys.
     */
    public FibonacciHeap(boolean max, Comparator<K> comparator) {
        this.max = max;
        this.comparator = comparator;
        // For a max-heap, reverse the comparator so that extraction returns the maximum.
        this.heapComparator = max ? (a, b) -> -comparator.compare(a, b) : comparator;
        this.min = null;
        this.n = 0;
    }

    /**
     * Convenience constructor which defaults to a max‑heap.
     *
     * @param comparator the comparator for the keys.
     */
    public FibonacciHeap(Comparator<K> comparator) {
        this(true, comparator);
    }

    /**
     * Returns true if the Fibonacci Heap is empty.
     *
     * @return true if the heap contains no elements.
     */
    public boolean isEmpty() {
        return min == null;
    }

    /**
     * Returns the number of elements in the Fibonacci Heap.
     *
     * @return the number of elements.
     */
    public int size() {
        return n;
    }

    /**
     * Inserts the given key into the Fibonacci Heap.
     *
     * @param key the key to insert.
     */
    public void give(K key) {
        Node<K> node = new Node<>(key);
        if (min == null) {
            min = node;
        } else {
            insertIntoRootList(node);
            if (heapComparator.compare(node.key, min.key) < 0) {
                min = node;
            }
        }
        n++;
    }

    /**
     * Removes and returns the element at the root of the heap.
     * If configured as a max‑heap, this returns the maximum element;
     * otherwise, it returns the minimum.
     *
     * @return the removed key.
     * @throws PQException if the heap is empty.
     */
    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        Node<K> z = min;
        if (z != null) {
            // For each child of z, add it to the root list.
            if (z.child != null) {
                List<Node<K>> children = new ArrayList<>();
                Node<K> x = z.child;
                do {
                    children.add(x);
                    x = x.right;
                } while (x != z.child);
                for (Node<K> child : children) {
                    removeNodeFromList(child);
                    insertIntoRootList(child);
                    child.parent = null;
                }
            }
            // Remove z from the root list.
            removeNodeFromList(z);
            if (z == z.right) {
                min = null;
            } else {
                min = z.right;
                consolidate();
            }
            n--;
        }
        return z.key;
    }

    /**
     * Returns an iterator over all keys in the Fibonacci Heap.
     * The ordering is not guaranteed.
     *
     * @return an iterator over the keys.
     */
    public Iterator<K> iterator() {
        List<K> keys = new ArrayList<>();
        if (min != null) {
            List<Node<K>> visited = new ArrayList<>();
            traverse(min, visited, keys);
        }
        return keys.iterator();
    }

    /**
     * Inserts a node into the root list.
     */
    private void insertIntoRootList(Node<K> node) {
        if (min == null) {
            node.left = node;
            node.right = node;
            min = node;
        } else {
            node.left = min;
            node.right = min.right;
            min.right.left = node;
            min.right = node;
        }
    }

    /**
     * Removes a node from its circular doubly linked list.
     */
    private void removeNodeFromList(Node<K> node) {
        node.left.right = node.right;
        node.right.left = node.left;
        node.left = node;
        node.right = node;
    }

    /**
     * Consolidates the trees in the root list by linking trees of the same degree.
     */
    private void consolidate() {
        int arraySize = ((int) Math.floor(Math.log(n) / Math.log(2))) + 1;
        List<Node<K>> A = new ArrayList<>(Collections.nCopies(arraySize, null));

        // Gather all nodes in the root list.
        List<Node<K>> rootNodes = new ArrayList<>();
        Node<K> x = min;
        if (x != null) {
            do {
                rootNodes.add(x);
                x = x.right;
            } while (x != min);
        }

        for (Node<K> w : rootNodes) {
            x = w;
            int d = x.degree;
            while (d < A.size() && A.get(d) != null) {
                Node<K> y = A.get(d);
                // Ensure that x has the smaller key according to heapComparator.
                if (heapComparator.compare(x.key, y.key) > 0) {
                    Node<K> temp = x;
                    x = y;
                    y = temp;
                }
                link(y, x);
                A.set(d, null);
                d++;
            }
            if (d >= A.size()) {
                for (int i = A.size(); i <= d; i++) {
                    A.add(null);
                }
            }
            A.set(d, x);
        }

        // Rebuild the root list and update the pointer to the minimum node.
        min = null;
        for (Node<K> node : A) {
            if (node != null) {
                if (min == null) {
                    node.left = node;
                    node.right = node;
                    min = node;
                } else {
                    insertIntoRootList(node);
                    if (heapComparator.compare(node.key, min.key) < 0) {
                        min = node;
                    }
                }
            }
        }
    }

    /**
     * Links two trees of the same degree by making y a child of x.
     *
     * @param y the node to become a child.
     * @param x the node to become the parent.
     */
    private void link(Node<K> y, Node<K> x) {
        removeNodeFromList(y);
        if (x.child == null) {
            y.left = y;
            y.right = y;
            x.child = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right.left = y;
            x.child.right = y;
        }
        y.parent = x;
        x.degree++;
        y.mark = false;
    }

    /**
     * Recursively traverses the Fibonacci Heap to collect all keys.
     *
     * @param start   the starting node.
     * @param visited list of nodes already visited.
     * @param keys    the list of keys collected.
     */
    private void traverse(Node<K> start, List<Node<K>> visited, List<K> keys) {
        if (start == null) return;
        Node<K> cur = start;
        do {
            if (!visited.contains(cur)) {
                visited.add(cur);
                keys.add(cur.key);
                if (cur.child != null) {
                    traverse(cur.child, visited, keys);
                }
            }
            cur = cur.right;
        } while (cur != start);
    }
}
