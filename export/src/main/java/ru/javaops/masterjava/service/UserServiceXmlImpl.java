package ru.javaops.masterjava.service;

import com.google.common.collect.ImmutableSet;
import ru.javaops.masterjava.da.UserDa;
import ru.javaops.masterjava.da.model.UserDaDto;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
class UserServiceXmlImpl implements UserServiceXml {

    private static final int NUMBER_THREADS = 4;
    private static final int SIZE_SET_FOR_SAVE = 100;

    private UserDa userDa = UserDa.getUserDaWithDataSourceFromJndi();

    private final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

    @Override
    public void saveUsersFromXmlToBD(final InputStream is) {
        try {
            final StaxStreamProcessor processor = new StaxStreamProcessor(is);
            final Set<UserDaDto> users = new HashSet<>();
            // Users loop
            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {

                final String email = processor.getAttribute("email");
                final String fullName = processor.getReader().getElementText();
                final UserDaDto forSaveToDB = new UserDaDto(fullName, email, "stubForCity");
                users.add(forSaveToDB);

                if (users.size() == SIZE_SET_FOR_SAVE) {
                    final Set<UserDaDto> forSave = ImmutableSet.copyOf(users);
                    users.clear();
                    executor.submit(() -> userDa.saveUsers(forSave));
                }
            }

            if (!users.isEmpty()) {
                executor.submit(() -> userDa.saveUsers(users));
            }

        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    //for test
    UserServiceXmlImpl withUserDa(UserDa userDa) {
        this.userDa = userDa;
        return this;
    }
}
