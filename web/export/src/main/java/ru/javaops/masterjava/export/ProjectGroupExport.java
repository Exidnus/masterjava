package ru.javaops.masterjava.export;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.export.results.GroupResult;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by dmitriy_varygin on 07.11.16.
 */
public class ProjectGroupExport extends BaseExport {

    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);

    public GroupResult process(final StaxStreamProcessor processor, final int size) throws XMLStreamException {

        final List<Project> projectsInDb = projectDao.getAll();
        final List<Group> buffer = new ArrayList<>(size);
        final List<Future<?>> results = new ArrayList<>();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "Project")) {
            final String projectName = processor.getAttribute("name");
            final int projectId = getProjectIdAndSaveProjectIfNeed(processor, projectsInDb, projectName);
            while (processor.doUntil(XMLEvent.START_ELEMENT, "Group")) {
                final String groupName = processor.getAttribute("name");
                final GroupType type = GroupType.valueOf(processor.getAttribute("type"));
                buffer.add(new Group(groupName, type, projectId));
                if (buffer.size() == size) {
                    final ImmutableList<Group> forSave = ImmutableList.copyOf(buffer);
                    buffer.clear();
                    final Future<?> result = executorService.submit(() -> groupDao.saveListWithoutSkippingSeq(forSave));
                    results.add(result);
                }
            }
        }
        if (!buffer.isEmpty()) {
            final Future<?> result = executorService.submit(() -> groupDao.saveListWithoutSkippingSeq(buffer));
            results.add(result);
        }

        results.forEach(r -> {
            try {
                r.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        return new GroupResult("Projects and groups: ");
    }

    private int getProjectIdAndSaveProjectIfNeed(final StaxStreamProcessor processor,
                                                 final List<Project> projectsInDb,
                                                 final String projectName) throws XMLStreamException {
        final Optional<Project> optProject = projectsInDb.stream()
                .filter(p -> p.getName().equals(projectName))
                .findAny();
        int projectId;
        if (optProject.isPresent()) {
            projectId = optProject.get().getId();
        } else {
            String description = null;
            while (processor.doUntil(XMLEvent.START_ELEMENT, "description")) {
                description = processor.getReader().getElementText();
                break;
            }
            Preconditions.checkNotNull(description);
            projectId = projectDao.insertGeneratedId(new Project(projectName, description));
        }
        return projectId;
    }
}
