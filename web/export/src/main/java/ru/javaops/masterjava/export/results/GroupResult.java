package ru.javaops.masterjava.export.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
public class GroupResult extends Result {
    public List<ChunkResult> chunkResults = new ArrayList<>();
    public int successful;
    public int failed;
    private final String prefix;

    public GroupResult(String prefix) {
        this.prefix = prefix;
    }

    public void add(ChunkResult chunkResult) {
        chunkResults.add(chunkResult);
        if (chunkResult.isOK()) {
            successful += chunkResult.getSize();
        } else {
            failed += chunkResult.getSize();
            result = isOK() ? chunkResult.toString() : "------------------------\n" + chunkResult.toString();
        }
    }

    @Override
    public String toString() {
        return prefix + "Result (successful=" + successful + ", failed=" + failed + "): " + result;
    }
}
