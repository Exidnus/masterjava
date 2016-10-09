package ru.javaops.masterjava.service;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import ru.javaops.masterjava.da.UserDa;
import ru.javaops.masterjava.da.model.UserDaDto;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
class UserServiceXmlImpl implements UserServiceXml {

    private UserDa userDa = UserDa.getUserDa();

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

                if (users.size() == 100) {
                    userDa.saveUsers(users);
                    users.clear();
                }
            }

            if (!users.isEmpty()) {
                userDa.saveUsers(users);
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
