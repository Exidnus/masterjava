package ru.javaops.masterjava.export;

import com.google.common.base.Splitter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.*;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Collections;
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
    private UserGroupDao userGroupDao = DBIProvider.getDao(UserGroupDao.class);

    public ProcessPayload.GroupResult process(final StaxStreamProcessor processor, Map<String, Group> groups, Map<String, City> cities, int chunkSize) throws XMLStreamException {
        log.info("Start proseccing with chunkSize=" + chunkSize);

        @Value
        class ChunkItem {
            private User user;
            private StreamEx<UserGroup> userGroups;
        }

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
                val result = new ProcessPayload.GroupResult();
                val chunkFutures = new ArrayList<ChunkFuture>();

                List<ChunkItem> chunk = new ArrayList<>(chunkSize);
                int id = userDao.getSeqAndSkip(chunkSize);

                while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    final String email = processor.getAttribute("email");
                    String cityRef = processor.getAttribute("city");
                    City city = cities.get(cityRef);
                    if (city == null) {
                        result.add(ProcessPayload.ChunkResult.createWithFail(email, "City '" + cityRef + "' is not present in DB"));
                    } else {
                        val groupRefs = processor.getAttribute("groupRefs");
                        List<String> groupNames = (groupRefs == null) ?
                                Collections.emptyList() :
                                Splitter.on(' ').splitToList(groupRefs);

                        if (!groups.keySet().containsAll(groupNames)) {
                            result.add(ProcessPayload.ChunkResult.createWithFail(email, "One of group from '" + groupRefs + "' is not present in DB"));
                        } else {
                            final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
                            final String fullName = processor.getText();
                            final User user = new User(id++, fullName, email, flag, city.getId());
                            StreamEx<UserGroup> userGroups = StreamEx.of(groupNames).map(name -> new UserGroup(user.getId(), groups.get(name).getId()));
                            chunk.add(new ChunkItem(user, userGroups));
                            if (chunk.size() == chunkSize) {
                                chunkFutures.add(submit(chunk));
                                chunk = new ArrayList<>(chunkSize);
                                id = userDao.getSeqAndSkip(chunkSize);
                            }
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

            private ChunkFuture submit(List<ChunkItem> chunk) {
                val users = StreamEx.of(chunk).map(ChunkItem::getUser).toList();
                ChunkFuture chunkFuture = new ChunkFuture(
                        users,
                        executorService.submit(() -> {
                            userDao.insertBatch(users);
                            userGroupDao.insertBatch(StreamEx.of(chunk).flatMap(ChunkItem::getUserGroups).toList());
                        }));
                log.info("Submit " + chunkFuture.chunkResult);
                return chunkFuture;
            }
        }.call();
    }
}
