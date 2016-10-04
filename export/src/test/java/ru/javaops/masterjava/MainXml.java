package ru.javaops.masterjava;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import j2html.tags.ContainerTag;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

/**
 * User: gkislin
 */
public class MainXml {
    public static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);
    public static final String PROJECT = "Project";
    public static final String USERS = "Users";
    public static final String GROUP = "Group";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Format: projectName, xmlName");
            System.exit(1);
        }
        URL payloadUrl = Resources.getResource(args[1]);
        MainXml main = new MainXml();
        String projectName = args[0];

        Set<User> users = main.parceByJaxb(projectName, payloadUrl);
        String out = outHtml(users, projectName, Paths.get("out/usersJaxb.html"));
        System.out.println(out);

        users = main.processByStax(projectName, payloadUrl);
        users.forEach(u -> System.out.println("Name: '" + u.getValue() + "', email: " + u.getEmail()));

        String html = main.transform(projectName, payloadUrl);
        System.out.println(html);
        try (Writer writer = Files.newBufferedWriter(Paths.get("out/groupsXslt.html"))) {
            writer.write(html);
        }
    }

    private String transform(String projectName, URL payloadUrl) throws Exception {
        URL xsl = Resources.getResource("groups.xsl");
        try (InputStream xmlStream = payloadUrl.openStream(); InputStream xslStream = xsl.openStream()) {
            XsltProcessor processor = new XsltProcessor(xslStream);

//        http://stackoverflow.com/questions/1667454/xsl-transformation-in-java-with-parameters
//        http://www.w3schools.com/xsl/el_param.asp
            processor.setParameter("projectName", projectName);
            return processor.transform(xmlStream);
        }
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

    private Set<User> parceByJaxb(String projectName, URL payloadUrl) throws Exception {
        JaxbParser parser = new JaxbParser(ObjectFactory.class);
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        try (InputStream is = payloadUrl.openStream()) {
            Payload payload = parser.unmarshal(is);
            Project project = StreamEx.of(payload.getProjects().getProject())
                    .filter(p -> p.getName().equals(projectName))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid project name '" + projectName + '\''));

            Set<Project.Group> groups = new HashSet<>(project.getGroup());  // identity compare
            return StreamEx.of(payload.getUsers().getUser())
                    .filter(u -> StreamEx.of(u.getGroupRefs())
                            .findAny(groups::contains)
                            .isPresent())
                    .collect(Collectors.toCollection(() -> new TreeSet<>(USER_COMPARATOR)));
        }
    }

    private static String outHtml(Set<User> users, String projectName, Path path) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            final ContainerTag table = table().with(tr().with(th("FullName"), th("email")));
            users.forEach(u -> table.with(tr().with(td(u.getValue()), td(u.getEmail()))));
            table.setAttribute("border", "1");
            table.setAttribute("cellpadding", "8");
            table.setAttribute("cellspacing", "0");

            String out = html().with(
                    head().with(title(projectName + " users")),
                    body().with(h1(projectName + " users"), table)
            ).render();
            writer.write(out);
            return out;
        }
    }
}
