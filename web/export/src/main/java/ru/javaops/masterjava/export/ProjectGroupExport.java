package ru.javaops.masterjava.export;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Map;

/**
 * gkislin
 * 15.11.2016
 */
@Slf4j
public class ProjectGroupExport {
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);

    public Map<String, Group> process(StaxStreamProcessor processor) throws XMLStreamException {
        val projectMap = projectDao.getAsMap();
        val groupMap = groupDao.getAsMap();
        String element;

        val newGroups = new ArrayList<Group>();
        Project project = null;
        while ((element = processor.doUntilAny(XMLEvent.START_ELEMENT, "Project", "Group", "Cities")) != null) {
            if (element.equals("Cities")) break;
            if (element.equals("Project")) {
                val name = processor.getAttribute("name");
                val description = processor.getElementValue("description");
                project = projectMap.get(name);
                if (project == null) {
                    project = new Project(name, description);
                    log.info("Insert project " + project);
                    projectDao.insert(project);
                }
            } else {
                val name = processor.getAttribute("name");
                if (!groupMap.containsKey(name)) {
                    newGroups.add(new Group(name, GroupType.valueOf(processor.getAttribute("type")), project.getId()));
                }
            }
        }
        log.info("Insert groups " + newGroups);
        groupDao.insertBatch(newGroups);
        return groupDao.getAsMap();
    }
}
