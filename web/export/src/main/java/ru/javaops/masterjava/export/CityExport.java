package ru.javaops.masterjava.export;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.export.results.ChunkResult;
import ru.javaops.masterjava.export.results.GroupResult;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by dmitriy_varygin on 07.11.16.
 */
@Slf4j
public class CityExport extends BaseExport {

    private final CityDao cityDao = DBIProvider.getDao(CityDao.class);

    public GroupResult process(final StaxStreamProcessor processor, final int size) throws XMLStreamException {
        log.info("Processing cities from xml.");
        final Set<String> idStrsInDb = cityDao.getAllIdStr();
        final List<ChunkFuture> chunkFutures = new ArrayList<>();
        final List<City> buffer = new ArrayList<>(size);
        while (processor.doUntilAndStopIfAnotherElement(XMLEvent.START_ELEMENT, "City")) {
            final String idStr = processor.getAttribute("id");
            if (!idStrsInDb.contains(idStr)) {
                buffer.add(new City(idStr, processor.getReader().getElementText()));
            }

            if (buffer.size() == size) {
                chunkFutures.add(submit(ImmutableList.copyOf(buffer)));
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            chunkFutures.add(submit(buffer));
        }

        return getGroupResultFromFutures(chunkFutures, new GroupResult("Cities: "));
    }

    private ChunkFuture submit(List<City> chunk) {
        ChunkFuture chunkFuture = new ChunkFuture(
                new ChunkResult(chunk.get(0).getName(), chunk.get(chunk.size() - 1).getName(), chunk.size()),
                executorService.submit(() -> {
                    cityDao.saveListWithoutSkippingSeq(chunk);
                }));
        log.info("Submit " + chunkFuture.getChunkResult());
        return chunkFuture;
    }
}
