/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc.randomwalk;

import java.util.Random;

/**
 * The RandomWalk class simulates a two-dimensional random walk. A "drunkard"
 * moves in a random direction for a specified number of steps, and the distance
 * from the starting point is measured. Additionally, multiple random walk
 * experiments can be performed to compute average distances.
 */
public class RandomWalk {

    private int x = 0;
    private int y = 0;

    private final Random random = new Random();

    /**
     * Method to compute the distance from the origin (the lamp-post where the drunkard starts) to his current position.
     *
     * @return the (Euclidean) distance from the origin to the current position.
     */
    public double distance() {
        // Euclidean distance in a 2D space is defined by the root of the sum of the squares of the corresponding coordinates on either axis
        // Since the origin is (0,0) it can be omitted from the formula
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * Private method to move the current position, that's to say the drunkard moves
     *
     * @param dx the distance he moves in the x direction
     * @param dy the distance he moves in the y direction
     */
    private void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Perform a random walk of m steps
     *
     * @param m the number of steps the drunkard takes
     */
    private void randomWalk(int m) {
        for (int i = 0; i < m; i++) {
            randomMove();
        }
    }

    /**
     * Private method to generate a random move according to the rules of the situation.
     * That's to say, moves can be (+-1, 0) or (0, +-1).
     */
    private void randomMove() {
        boolean ns = random.nextBoolean();
        int step = random.nextBoolean() ? 1 : -1;
        move(ns ? step : 0, ns ? 0 : step);
    }

    /**
     * Perform multiple random walk experiments, returning the mean distance.
     *
     * @param m the number of steps for each experiment
     * @param n the number of experiments to run
     * @return the mean distance
     */
    public static double randomWalkMulti(int m, int n) {
        double totalDistance = 0;
        for (int i = 0; i < n; i++) {
            RandomWalk walk = new RandomWalk();
            walk.randomWalk(m);
            totalDistance = totalDistance + walk.distance();
        }
        return totalDistance / n;
    }

    /**
     * The main method serves as the entry point to the RandomWalk program. It performs
     * either a single random walk experiment or several experiments, based on the
     * provided input arguments, and prints the mean distance.
     *
     * @param args command-line arguments where:
     *             args[0 ... n-2] specifies the number of steps for a random walk (required),
     *             and args[n-1] optionally specifies the number of experiments (default is 30).
     *             If args is empty, the method throws a RuntimeException indicating invalid syntax.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("Syntax: RandomWalk steps [experiments]");
        }
        int[] stepsArray = new int[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            stepsArray[i] = Integer.parseInt(args[i]);
        }
        int n = 30;
        if (args.length > 1) {
            n = Integer.parseInt(args[args.length - 1]);
        }

        System.out.println("Random Walk Experiments for " + n + " steps");
        System.out.println("Steps (m)\tMean Distance (d)\tSquare Root of the Steps (m ^ 0.5)");

        for (int m : stepsArray) {
            double meanDistance = randomWalkMulti(m, n);
            System.out.printf("\t%d\t\t\t\t%.2f\t\t\t\t%.2f\n", m, meanDistance, Math.pow(m, 0.5)); //Had to format this
        }
    }
}