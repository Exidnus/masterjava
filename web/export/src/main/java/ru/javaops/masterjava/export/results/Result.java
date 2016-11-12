package ru.javaops.masterjava.export.results;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
public class Result {
    private static final String OK = "OK";

    public String result = OK;

    public boolean isOK() {
        return OK.equals(result);
    }
}