package ru.javaops.masterjava.export;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.export.results.ChunkResult;
import ru.javaops.masterjava.export.results.GroupResult;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.*;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * gkislin
 * 14.10.2016
 */
@Slf4j
public class UserExport extends BaseExport {

    private final UserDao userDao = DBIProvider.getDao(UserDao.class);
    private final CityDao cityDao = DBIProvider.getDao(CityDao.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    public GroupResult process(final StaxStreamProcessor processor, int chunkSize) throws XMLStreamException {
        log.info("Start proseccing with chunkSize=" + chunkSize);

        return new Callable<GroupResult>() {

            @Override
            public GroupResult call() throws XMLStreamException {
                List<ChunkFuture> chunkFutures = new ArrayList<>();
                List<User> chunk = new ArrayList<>(chunkSize);
                int id = userDao.getSeqAndSkip(chunkSize);

                final Map<String, Integer> idStrsToIdsCities = cityDao.getAll()
                        .stream()
                        .collect(Collectors.toMap(City::getIdStr, City::getId));

                final Map<String, Integer> groupNamesToIds = groupDao.getAll()
                        .stream()
                        .collect(Collectors.toMap(Group::getName, Group::getId));

                while (processor.doUntilAndStopIfAnotherElement(XMLEvent.START_ELEMENT, "User")) {
                    processGroups(id, processor.getAttribute("groupRefs"), groupNamesToIds);
                    final String email = processor.getAttribute("email");
                    final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
                    final String cityIdAsStr = processor.getAttribute("city");
                    final String fullName = processor.getReader().getElementText();
                    Preconditions.checkArgument(!Strings.isNullOrEmpty(cityIdAsStr));
                    final int cityId = idStrsToIdsCities.get(cityIdAsStr);
                    final User user = new User(id++, fullName, email, flag, cityId);
                    chunk.add(user);
                    if (chunk.size() == chunkSize) {
                        chunkFutures.add(submit(chunk));
                        chunk = new ArrayList<>(chunkSize);
                        id = userDao.getSeqAndSkip(chunkSize);
                    }
                }
                if (!chunk.isEmpty()) {
                    chunkFutures.add(submit(chunk));
                }

                return getGroupResultFromFutures(chunkFutures, new GroupResult("Users: "));
            }

            private ChunkFuture submit(List<User> chunk) {
                ChunkFuture chunkFuture = new ChunkFuture(
                        new ChunkResult(chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail(), chunk.size()),
                        executorService.submit(() -> {
                            userDao.saveChunk(chunk);
                        }));
                log.info("Submit " + chunkFuture.getChunkResult());
                return chunkFuture;
            }

            private void processGroups(final int userId, final String groupsAsString,
                                       final Map<String, Integer> groupNamesToIds) {
                final List<UserGroup> userGroups = Util.getGroupNamesFromString(groupsAsString)
                        .stream()
                        .map(groupNamesToIds::get)
                        .map(groupId -> new UserGroup(userId, groupId))
                        .collect(Collectors.toList());
                groupDao.saveUserGroups(userGroups);
            }
        }.call();
    }
}
