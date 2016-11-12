package ru.javaops.masterjava.export.results;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ChunkResult extends Result {
    String startEmail;
    String endEmail;
    int size;

    public void setFail(String message) {
        result = message;
    }

    @Override
    public String toString() {
        return "Chunk (startEmail='" + startEmail + '\'' + ", endEmail='" + endEmail + "', size:'" + size + "):" + result;
    }
}
