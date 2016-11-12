package ru.javaops.masterjava.export;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.export.results.ChunkResult;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by dmitriy_varygin on 07.11.16.
 */
@Slf4j
public class ProjectGroupExport extends BaseExport {

    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);

    public GroupResult process(final StaxStreamProcessor processor, final int size) throws XMLStreamException {

        final List<Project> projectsInDb = projectDao.getAll();
        final List<Group> buffer = new ArrayList<>(size);
        final List<ChunkFuture> chunkFutures = new ArrayList<>();

        Preconditions.checkArgument(processor.doUntil(XMLEvent.START_ELEMENT, "Project"));
        boolean isNextElementProject;
        do {
            final String projectName = processor.getAttribute("name");
            final int projectId = getProjectIdAndSaveProjectIfNeed(processor, projectsInDb, projectName);
            boolean isNextElementGroup = true;
            while (isNextElementGroup) {
                final String nextElementName = processor.getNextElementName();
                if (nextElementName.equals("Group")) {
                    final String groupName = processor.getAttribute("name");
                    final GroupType type = GroupType.valueOf(processor.getAttribute("type"));
                    buffer.add(new Group(groupName, type, projectId));
                } else {
                    isNextElementGroup = false;
                }
                processor.getReader().next();
            }
            processor.getReader().next();
            final String nextElementName = processor.getReader().getLocalName();
            isNextElementProject = nextElementName.equals("Project");
        } while (isNextElementProject);

        if (!buffer.isEmpty()) {
            chunkFutures.add(submit(buffer));
        }

        return getGroupResultFromFutures(chunkFutures, new GroupResult("Groups: "));
    }

    private ChunkFuture submit(List<Group> chunk) {
        ChunkFuture chunkFuture = new ChunkFuture(
                new ChunkResult(chunk.get(0).getName(), chunk.get(chunk.size() - 1).getName(), chunk.size()),
                executorService.submit(() -> {
                    groupDao.saveListWithoutSkippingSeq(chunk);
                }));
        log.info("Submit " + chunkFuture.getChunkResult());
        return chunkFuture;
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
            final String description = Objects.requireNonNull(processor.getElementValue("description"));
            projectId = projectDao.insertGeneratedId(new Project(projectName, description));
        }
        return projectId;
    }
}
