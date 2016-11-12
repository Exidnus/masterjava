package ru.javaops.masterjava.export;

import lombok.Getter;
import ru.javaops.masterjava.export.results.ChunkResult;

import java.util.concurrent.Future;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
class ChunkFuture {
    @Getter private ChunkResult chunkResult;
    @Getter private Future future;

    ChunkFuture(ChunkResult chunkResult, Future future) {
        this.chunkResult = chunkResult;
        this.future = future;
    }
}
