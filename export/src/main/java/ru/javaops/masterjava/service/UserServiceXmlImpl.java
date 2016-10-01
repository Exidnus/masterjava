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

    private static final String PROJECT = "Project";
    private static final String USERS = "Users";
    private static final String GROUP = "Group";

    private UserDa userDa = UserDa.getUserDa();

    @Override
    public void saveUsersFromXmlToBD(final String projectName, final InputStream is) {
        try {
            final StaxStreamProcessor processor = new StaxStreamProcessor(is);
            final Set<String> groupNames = new HashSet<>();
            String element;
            // Projects loop
            projects:
            while (processor.doUntil(XMLEvent.START_ELEMENT, PROJECT)) {
                if (projectName.equals(processor.getAttribute("name"))) {
                    // Groups loop
                    while ((element = processor.doUntilAny(XMLEvent.START_ELEMENT, PROJECT, GROUP, USERS)) != null) {
                        if (!element.equals(GROUP)) {
                            break projects;
                        }
                        groupNames.add(processor.getAttribute("name"));
                    }
                }
            }
            if (groupNames.isEmpty()) {
                throw new IllegalArgumentException("Invalid " + projectName + " or no groups");
            }
            final Set<UserDaDto> users = new HashSet<>();
            // Users loop
            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                String groupRefs = processor.getAttribute("groupRefs");

//   http://stackoverflow.com/questions/8708542/something-like-contains-any-for-java-set
                if (StringUtils.isEmpty(groupRefs)) continue;

                for (String ref : Splitter.on(' ').split(groupRefs)) {
                    if (groupNames.contains(ref)) {
                        final String email = processor.getAttribute("email");
                        final String fullName = processor.getReader().getElementText();
                        final UserDaDto forSaveToDB = new UserDaDto(fullName, email, "stubForCity");
                        users.add(forSaveToDB);
                        break;
                    }
                }

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
