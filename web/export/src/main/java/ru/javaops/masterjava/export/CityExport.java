package ru.javaops.masterjava.export;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dmitriy_varygin on 07.11.16.
 */
public class CityExport {

    private static final int NUMBER_THREADS = 4;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
    private final CityDao cityDao = DBIProvider.getDao(CityDao.class);

    public void process(final InputStream is, final int size) throws XMLStreamException {
        try (final StaxStreamProcessor processor = new StaxStreamProcessor(is)) {
            final Set<String> idStrsInDb = cityDao.getAllIdStr();
            final List<City> buffer = new ArrayList<>(size);
            final List<Future<?>> results = new ArrayList<>();
            while (processor.doUntil(XMLEvent.START_ELEMENT, "City")) {
                final String idStr = processor.getAttribute("id");
                if (!idStrsInDb.contains(idStr)) {
                    buffer.add(new City(idStr, processor.getReader().getElementText()));
                }

                if (buffer.size() == size) {
                    final List<City> forSaveInBd = ImmutableList.copyOf(buffer);
                    buffer.clear();
                    final Future<?> result = executorService.submit(() -> cityDao.saveListWithoutSkippingSeq(forSaveInBd));
                    results.add(result);
                }
            }

            if (!buffer.isEmpty()) {
                final Future<?> result = executorService.submit(() -> cityDao.saveListWithoutSkippingSeq(buffer));
                results.add(result);
            }

            for (Future<?> result : results) {
                try {
                    result.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException();
                }
            }
        }

    }
}
