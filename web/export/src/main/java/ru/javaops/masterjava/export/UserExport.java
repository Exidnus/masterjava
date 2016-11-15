package ru.javaops.masterjava.export;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * gkislin
 * 14.10.2016
 */
@Slf4j
public class UserExport {

    private static final int NUMBER_THREADS = 4;
    private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);
    private UserDao userDao = DBIProvider.getDao(UserDao.class);

    public ProcessPayload.GroupResult process(final StaxStreamProcessor processor, Map<String, City> cities, int chunkSize) throws XMLStreamException {
        log.info("Start proseccing with chunkSize=" + chunkSize);

        return new Callable<ProcessPayload.GroupResult>() {
            class ChunkFuture {
                private ProcessPayload.ChunkResult chunkResult;
                private Future future;

                public ChunkFuture(List<User> users, Future future) {
                    int size = users.size();
                    this.chunkResult = new ProcessPayload.ChunkResult(users.get(0).getEmail(), users.get(size - 1).getEmail(), size);
                    this.future = future;
                }
            }

            @Override
            public ProcessPayload.GroupResult call() throws XMLStreamException {
                ProcessPayload.GroupResult result = new ProcessPayload.GroupResult();
                List<ChunkFuture> chunkFutures = new ArrayList<>();

                List<User> chunk = new ArrayList<>(chunkSize);
                int id = userDao.getSeqAndSkip(chunkSize);

                while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    final String email = processor.getAttribute("email");
                    String cityRef = processor.getAttribute("city");
                    City city = cities.get(cityRef);
                    if (city == null) {
                        result.add(ProcessPayload.ChunkResult.createWithFail(email, "City '" + cityRef + "' is not present in DB"));
                    } else {
                        final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
                        final String fullName = processor.getText();
                        final User user = new User(id++, fullName, email, flag, city.getId());

                        chunk.add(user);
                        if (chunk.size() == chunkSize) {
                            chunkFutures.add(submit(chunk));
                            chunk = new ArrayList<>(chunkSize);
                            id = userDao.getSeqAndSkip(chunkSize);
                        }
                    }
                }
                if (!chunk.isEmpty()) {
                    chunkFutures.add(submit(chunk));
                }
                chunkFutures.forEach(cf -> {
                    try {
                        cf.future.get();
                    } catch (Exception e) {
                        cf.chunkResult.setFail(e.getMessage());
                    }
                    result.add(cf.chunkResult);
                });
                return result;
            }

            private ChunkFuture submit(List<User> chunk) {
                ChunkFuture chunkFuture = new ChunkFuture(
                        chunk,
                        executorService.submit(() -> {
                            userDao.insertBatch(chunk);
                        }));
                log.info("Submit " + chunkFuture.chunkResult);
                return chunkFuture;
            }
        }.call();
    }
}
