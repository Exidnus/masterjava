package ru.javaops.masterjava.export;

import ru.javaops.masterjava.export.results.GroupResult;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dmitriy_varygin on 09.11.16.
 */
abstract class BaseExport {

    private static final int NUMBER_THREADS = 4;
    protected final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

    public abstract GroupResult process(final StaxStreamProcessor processor, final int chunkSize) throws XMLStreamException;
}
