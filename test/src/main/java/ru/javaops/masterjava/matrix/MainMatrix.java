package ru.javaops.masterjava.matrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * gkislin
 * 03.07.2016
 */
public class MainMatrix {
    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_NUMBER = 10;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        final int[][] matrixC = MatrixUtil.singleThreadMultiplyOpt(matrixA, matrixB);

        int[][] concurrentMatrixC = MatrixUtil.concurrentMultiplyDarthVader(matrixA, matrixB, executor);
        compare(matrixC, concurrentMatrixC);

        concurrentMatrixC = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
        compare(matrixC, concurrentMatrixC);

        concurrentMatrixC = MatrixUtil.concurrentMultiply2(matrixA, matrixB, executor);
        compare(matrixC, concurrentMatrixC);

        concurrentMatrixC = MatrixUtil.concurrentMultiplyArray(matrixA, matrixB, executor);
        compare(matrixC, concurrentMatrixC);

        executor.shutdown();
    }

    private static void compare(int[][] matrix1, int[][] matrix2) {
        if (!MatrixUtil.compare(matrix1, matrix2)) {
            System.err.println("Comparison failed");
        } else {
            System.out.println("OK");
        }
    }
}
