package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import j2html.tags.Tag;
import ru.javaops.masterjava.xml.schema.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static j2html.TagCreator.*;

/**
 * @author Varygin DV
 * http://j2html.com
 */
public class ConsoleApp {

    //TODO make a program argument?
    private static final String PATH_TO_RESULT_HTML = "F:\\list_participants.html";

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
        final String htmlAsString = getHtmlAsString(participants);
        makeHtmlFile(htmlAsString);
    }

    private Payload getPayload() throws Exception {
        final JaxbParser parser = new JaxbParser(ObjectFactory.class);
        return parser.unmarshal(Resources.getResource(xmlPath).openStream());
    }

    private List<Participant> getParticipants(final Payload payload, final String projectName) {
        final List<GroupMember> groupMembers = payload.getProjects().getProject().stream()
                .filter(project -> project.getName().equals(projectName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("There is not project with name " + projectName))
                .getGroup()
                .stream()
                .map(Group::getGroupMember)
                .reduce(this::unionLists)
                .orElse(Collections.emptyList());

        return getDistinctAndSortedParticipants(groupMembers);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> unionLists(List<T> first, List<T> second) {
        final List<T> union = new ArrayList<>();
        Collections.addAll(union, (T[]) first.toArray());
        Collections.addAll(union, (T[]) second.toArray());
        return union;
    }

    private List<Participant> getDistinctAndSortedParticipants(final List<GroupMember> groupMembers) {
        return groupMembers.stream()
                .map(g -> (Participant) g.getIdParticipant())
                .distinct()
                .sorted((p1, p2) -> {
                    final int idCompared = p1.getId().compareTo(p2.getId());
                    if (idCompared == 0) {
                        final int lastNameCompared = p1.getLastName().compareTo(p2.getLastName());
                        if (lastNameCompared == 0) {
                            return p1.getFirstName().compareTo(p2.getFirstName());
                        } else {
                            return lastNameCompared;
                        }
                    } else {
                        return idCompared;
                    }
                })
                .collect(Collectors.toList());
    }

    private String getHtmlAsString(final List<Participant> participants) {
        final Tag html = html().with(
                head().with(
                        title(projectName)
                ),
                body().with(
                        h1("Participants of " + projectName),
                        getTable(participants)
                )
        );

        return html.render();
    }

    private Tag getTable(final List<Participant> participants) {
        final List<Tag> participantsAsTableRows = participants.stream()
                .map(p -> tr().with(
                        td(p.getId()), td(p.getFirstName()), td(p.getLastName())
                ))
                .collect(Collectors.toList());

        participantsAsTableRows.add(0, tr().with(td("id"), td("First Name"), td("Last Name")));

        return table().with(participantsAsTableRows);
    }

    private void makeHtmlFile(final String htmlAsString) throws IOException {
        try (OutputStream output = new BufferedOutputStream(Files.newOutputStream(Paths.get(PATH_TO_RESULT_HTML)))) {
            output.write(htmlAsString.getBytes());
        }
    }
}
