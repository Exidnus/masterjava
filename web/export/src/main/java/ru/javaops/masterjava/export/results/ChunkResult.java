package ru.javaops.masterjava.export.results;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ChunkResult extends Result {
    String first;
    String last;
    int size;

    public void setFail(String message) {
        result = message;
    }

    @Override
    public String toString() {
        return "Chunk (first='" + first + '\'' + ", last='" + last + "', size:'" + size + "):" + result;
    }
}
