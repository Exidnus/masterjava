package ru.javaops.masterjava.matrix;

import javafx.util.Pair;

/**
 * Created by dmitriy_varygin on 10.07.16.
 */
public class IncrementHelper {

    private volatile int x = 0;
    private volatile int y = 0;

    public synchronized Pair<Integer, Integer> getAndIncrement() {
        Pair<Integer, Integer> result = new Pair<>(x, y);
        y++;
        if (y >= MainMatrix.MATRIX_SIZE) {
            y = 0;
            x++;
        }
        return result;
    }

    public boolean haveMoreWork() {
        return x < MainMatrix.MATRIX_SIZE;
    }
}
