package ru.javaops.masterjava.export;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javaops.masterjava.model.User;
import ru.javaops.masterjava.model.UserFlag;
import ru.javaops.masterjava.persist.UserDao;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * gkislin
 * 14.10.2016
 */
public class UserExport {

    private static final Logger LOG = LoggerFactory.getLogger(UserExport.class);

    private static final int NUMBER_THREADS = 4;
    private static final int SIZE_COLLECTION_FOR_SAVE = 100;

    private UserDao userDao = UserDao.getUserDaoDefault();

    private final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

    public List<User> process(final InputStream is) throws XMLStreamException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        final List<User> buffer = new ArrayList<>(SIZE_COLLECTION_FOR_SAVE);
        final List<User> all = new ArrayList<>();
        final List<Future<?>> savings = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final UserFlag flag = UserFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final User user = new User(fullName, email, flag);
            buffer.add(user);

            if (buffer.size() == SIZE_COLLECTION_FOR_SAVE) {
                final List<User> forSave = ImmutableList.copyOf(buffer);
                all.addAll(forSave);
                buffer.clear();
                final Future<?> saving = executor.submit(() -> userDao.save(forSave));
                savings.add(saving);
            }
        }

        if (!buffer.isEmpty()) {
            all.addAll(buffer);
            final Future<?> saving = executor.submit(() -> userDao.save(buffer));
            savings.add(saving);
        }

        for (Future<?> current : savings) {
            try {
                current.get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("Failed processing xml and saving users.", e);
            }
        }

        return all;
    }

    UserExport withUserDaoOnlyForTest(UserDao userDao) {
        this.userDao = userDao;
        return this;
    }
}
