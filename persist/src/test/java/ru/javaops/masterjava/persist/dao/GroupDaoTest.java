package ru.javaops.masterjava.persist.dao;

import org.junit.Test;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

import static org.junit.Assert.*;
import static ru.javaops.masterjava.persist.model.GroupType.CURRENT;
import static ru.javaops.masterjava.persist.model.GroupType.FINISHED;

/**
 * Created by dmitriy_varygin on 06.11.16.
 */
public class GroupDaoTest extends AbstractDaoTest<GroupDao> {

    private static final Group TOPJAVA_06 = new Group("topjava06", FINISHED, 1);
    private static final Group MASTERJAVA_01 = new Group("masterjava01", CURRENT, 1);

    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);

    public GroupDaoTest() {
        super(GroupDao.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        projectDao.clean();
        projectDao.insertWithId(new Project(1, "stub", "stub"));
        dao.insertGeneratedId(TOPJAVA_06);
        dao.insertGeneratedId(MASTERJAVA_01);
    }

    @Test
    public void shouldGetAll() {
        final List<Group> all = dao.getAll();
        assertNotNull(all);
        System.out.println(all.size());
        assertTrue(all.size() == 2);
        final long count = all.stream()
                .filter(g -> ("topjava06".equals(g.getName()) && FINISHED == g.getType() ||
                        ("masterjava01".equals(g.getName()) && CURRENT == g.getType())))
                .count();
        assertTrue(count == 2);
    }
}