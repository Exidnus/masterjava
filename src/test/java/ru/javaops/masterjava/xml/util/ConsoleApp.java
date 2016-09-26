package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Varygin DV
 */
public class ConsoleApp {

    public static void main(String[] args) throws Exception {
        checkArgument(args.length >= 2, "You should specify project name and path to xml");
        new ConsoleApp(args[0], args[1]).printHtml();
    }

    private final String projectName;
    private final String xmlPath;

    public ConsoleApp(String projectName, String xmlPath) {
        this.projectName = projectName;
        this.xmlPath = xmlPath;
    }

    public void printHtml() throws Exception {
        final Payload payload = getPayload();
        final List<Participant> participants = getParticipants(payload, projectName);
        participants.forEach(System.out::println);
    }

    private Payload getPayload() throws Exception {
        final JaxbParser parser = new JaxbParser(ObjectFactory.class);
        return parser.unmarshal(Resources.getResource(xmlPath).openStream());
    }

    private List<Participant> getParticipants(final Payload payload, final String projectName) {
        List<GroupMember> groupMembers = payload.getProjects().getProject().stream()
                .filter(project -> project.getName().equals(projectName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("There is not project with name " + projectName))
                .getGroup()
                .stream()
                .map(Group::getGroupMember)
                .reduce(this::unionLists)
                .orElse(Collections.emptyList());

        return groupMembers.stream()
                .map(g -> (Participant) g.getIdParticipant())
                .distinct()
                .collect(Collectors.toList());
    }

    private <T> List<T> unionLists(List<T> first, List<T> second) {
        first.forEach(second::add);
        return second;
    }
}
