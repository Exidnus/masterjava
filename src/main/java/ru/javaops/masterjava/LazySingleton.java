package ru.javaops.masterjava;

/**
 * gkislin
 * 26.07.2016
 */
public class LazySingleton {
    private static LazySingleton instance;
    private final int i;

    private static class LazyHolder {
        private static final LazySingleton INSTANCE = new LazySingleton();
    }

    public static LazySingleton getInstance() {
        return LazyHolder.INSTANCE;
/*
        if (instance == null) {
            synchronized (LazySingleton.class) {
                if (instance == null) {
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
*/
    }

    private LazySingleton() {
        i = 5 + 8;
    }
}
