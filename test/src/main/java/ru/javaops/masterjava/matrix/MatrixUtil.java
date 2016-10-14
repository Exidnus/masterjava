package ru.javaops.masterjava.matrix;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;

        class ColumnMultipleResult {
            private final int col;
            private final int[] columnC;

            private ColumnMultipleResult(int col, int[] columnC) {
                this.col = col;
                this.columnC = columnC;
            }
        }

        final CompletionService<ColumnMultipleResult> completionService = new ExecutorCompletionService<>(executor);

        for (int j = 0; j < matrixSize; j++) {
            final int col = j;
            final int[] columnB = new int[matrixSize];
            for (int k = 0; k < matrixSize; k++) {
                columnB[k] = matrixB[k][col];
            }
            completionService.submit(() -> {
                final int[] columnC = new int[matrixSize];

                for (int row = 0; row < matrixSize; row++) {
                    final int[] rowA = matrixA[row];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * columnB[k];
                    }
                    columnC[row] = sum;
                }
                return new ColumnMultipleResult(col, columnC);
            });
        }

        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            ColumnMultipleResult res = completionService.take().get();
            for (int k = 0; k < matrixSize; k++) {
                matrixC[k][res.col] = res.columnC[k];
            }
        }
        return matrixC;
    }

    public static int[][] concurrentMultiplyArray(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int totalSize = matrixSize * matrixSize;
        final int[][] matrixC = new int[matrixSize][];

        final int[] matrixBT = new int[totalSize];
        for (int i = 0; i < matrixSize; i++) {
            int[] rowB = matrixB[i];
            int offset = i;
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[offset] = rowB[j];
                offset += matrixSize;
            }
        }

        List<Callable<Void>> tasks = new ArrayList<>(matrixSize);
        for (int j = 0; j < matrixSize; j++) {
            final int row = j;
            final int[] rowA = matrixA[row];
            tasks.add(() -> {
                final int[] rowC = new int[matrixSize];
                int offset = 0;
                for (int col = 0; col < matrixSize; col++) {
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * matrixBT[offset + k];
                    }
                    rowC[col] = sum;
                    offset += matrixSize;
                }
                matrixC[row] = rowC;
                return null;
            });
        }
        executor.invokeAll(tasks);
        return matrixC;
    }

    public static int[][] concurrentMultiplyDarthVader(int[][] matrixA, int[][] matrixB, ExecutorService executor)
            throws InterruptedException, ExecutionException {

        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        List<Callable<Void>> tasks = IntStream.range(0, matrixSize)
                .parallel()
                .mapToObj(i -> new Callable<Void>() {
                    private final int[] tempColumn = new int[matrixSize];

                    @Override
                    public Void call() throws Exception {
                        for (int c = 0; c < matrixSize; c++) {
                            tempColumn[c] = matrixB[c][i];
                        }
                        for (int j = 0; j < matrixSize; j++) {
                            int row[] = matrixA[j];
                            int sum = 0;
                            for (int k = 0; k < matrixSize; k++) {
                                sum += tempColumn[k] * row[k];
                            }
                            matrixC[j][i] = sum;
                        }
                        return null;
                    }
                })
                .collect(toList());

        executor.invokeAll(tasks);
        return matrixC;
    }

    public static int[][] concurrentMultiply2(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][];

        final int[][] matrixBT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[i][j] = matrixB[j][i];
            }
        }

        List<Callable<Void>> tasks = new ArrayList<>(matrixSize);
        for (int j = 0; j < matrixSize; j++) {
            final int row = j;
            final int[] rowA = matrixA[row];
            tasks.add(() -> {
                final int[] rowC = new int[matrixSize];
                for (int col = 0; col < matrixSize; col++) {
                    final int[] columnB = matrixBT[col];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * columnB[k];
                    }
                    rowC[col] = sum;
                }
                matrixC[row] = rowC;
                return null;
            });
        }
        executor.invokeAll(tasks);
        return matrixC;
    }

    public static int[][] singleThreadMultiplyOpt(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int col = 0; col < matrixSize; col++) {
            final int[] columnB = new int[matrixSize];
            for (int k = 0; k < matrixSize; k++) {
                columnB[k] = matrixB[k][col];
            }

            for (int row = 0; row < matrixSize; row++) {
                int sum = 0;
                final int[] rowA = matrixA[row];
                for (int k = 0; k < matrixSize; k++) {
                    sum += rowA[k] * columnB[k];
                }
                matrixC[row][col] = sum;
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
