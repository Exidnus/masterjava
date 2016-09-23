package ru.javaops.masterjava.matrix;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] multiplyWithOnlyJava14(int[][] matrix1, int[][] matrix2) {

        final IncrementHelper helper = new IncrementHelper();
        int[][] result = new int[matrix1.length][matrix1.length];
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < MainMatrix.THREAD_NUMBER - 1; i++) {
            Thread worker = new Thread(() -> {
                while (helper.haveMoreWork() && !Thread.currentThread().isInterrupted()) {
                    Pair<Integer, Integer> current = helper.getAndIncrement();
                    final int x = current.getKey();
                    final int y = current.getValue();
                    int sum = 0;
                    for (int k = 0; k < matrix1.length; k++) {
                        sum += matrix1[x][k] * matrix2[k][y];
                    }
                    result[x][y] = sum;
                }
            });
            threads.add(worker);
        }

        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return result;
    }

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int[][] result = new int[matrixA.length][matrixA.length];
        final List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < matrixA.length; i++) {
            final int iCopy = i;
            final Runnable current = () -> {
                for (int y = 0; y < matrixA.length; y++) {
                    int resultInt = 0;
                    for (int k = 0; k < matrixA.length; k++) {
                        resultInt += matrixA[iCopy][k] * matrixB[k][y];
                    }
                    result[iCopy][y] = resultInt;
                }
            };
            tasks.add(current);
        }

        final List<? extends Future<?>> futures = tasks.stream()
                .map(executor::submit)
                .collect(Collectors.toList());
        for (Future future : futures) {
            future.get();
        }
        return result;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
