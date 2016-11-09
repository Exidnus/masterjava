package ru.javaops.masterjava.export;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dmitriy_varygin on 09.11.16.
 */
abstract class BaseExport {

    private static final int NUMBER_THREADS = 4;
    protected final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
}
