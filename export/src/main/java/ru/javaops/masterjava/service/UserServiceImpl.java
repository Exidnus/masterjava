package ru.javaops.masterjava.service;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import ru.javaops.masterjava.da.UserDa;
import ru.javaops.masterjava.da.UserDaJdbcTemplateImpl;
import ru.javaops.masterjava.da.model.UserDaDto;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public class UserServiceImpl implements UserService {

    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);
    private static final String PROJECT = "Project";
    private static final String USERS = "Users";
    private static final String GROUP = "Group";

    private final UserDa userDa = new UserDaJdbcTemplateImpl();

    @Override
    public void saveUsersFromXmlToBD(String projectName, URL source) {
        try (final InputStream is = source.openStream()) {
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
                        /*User user = new User();
                        user.setEmail(processor.getAttribute("email"));
                        user.setValue(processor.getReader().getElementText());
                        users.add(user);*/
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

            userDa.saveUsers(users);

        } catch (IOException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
        //TODO read 100 users - save, etc. Not all in one time.
    }

    private Set<User> processByStax(String projectName, URL payloadUrl) throws Exception {
        try (InputStream is = payloadUrl.openStream()) {
            StaxStreamProcessor processor = new StaxStreamProcessor(is);
            Set<String> groupNames = new HashSet<>();
            Set<User> users = new TreeSet<>(USER_COMPARATOR);
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

            // Users loop
            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                String groupRefs = processor.getAttribute("groupRefs");

//   http://stackoverflow.com/questions/8708542/something-like-contains-any-for-java-set
                if (StringUtils.isEmpty(groupRefs)) continue;

                for (String ref : Splitter.on(' ').split(groupRefs)) {
                    if (groupNames.contains(ref)) {
                        User user = new User();
                        user.setEmail(processor.getAttribute("email"));
                        user.setValue(processor.getReader().getElementText());
                        users.add(user);
                        break;
                    }
                }
            }
            return users;
        }
    }
}
