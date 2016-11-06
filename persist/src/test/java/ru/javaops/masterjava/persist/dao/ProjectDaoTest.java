package ru.javaops.masterjava.persist.dao;

import org.junit.Test;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by dmitriy_varygin on 06.11.16.
 */
public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {

    private static final Project TOPJAVA = new Project("topjava", "Topjava");
    private static final Project MASTERJAVA = new Project("masterjava", "Masterjava");

    public ProjectDaoTest() {
        super(ProjectDao.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dao.insertGeneratedId(TOPJAVA);
        dao.insertGeneratedId(MASTERJAVA);
    }

    @Test
    public void shouldGetAll() throws Exception {
        final List<Project> all = dao.getAll();
        assertNotNull(all);
        assertEquals(all.size(), 2);
        final long count = all.stream()
                .filter(p -> ("topjava".equals(p.getName()) && "Topjava".equals(p.getDescription())) ||
                        ("masterjava".equals(p.getName()) && "Masterjava".equals(p.getDescription())))
                .count();
        assertEquals(count, 2);
    }
}