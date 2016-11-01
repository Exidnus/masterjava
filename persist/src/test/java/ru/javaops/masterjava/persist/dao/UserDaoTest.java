package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;

import java.util.Arrays;
import java.util.List;

/**
 * gkislin
 * 27.10.2016
 */
public class UserDaoTest extends AbstractDaoTest<UserDao> {
    private static final Logger LOG = LoggerFactory.getLogger(UserDaoTest.class);

    private static User ADMIN = new User("Admin", "admin@javaops.ru", UserFlag.superuser);
    private static User DELETED = new User("Deleted", "deleted@yandex.ru", UserFlag.deleted);
    private static User FULL_NAME = new User("Full Name", "gmail@gmail.com", UserFlag.active);
    private static User USER1 = new User("User1", "user1@gmail.com", UserFlag.active);
    private static User USER2 = new User("User2", "user2@yandex.ru", UserFlag.active);
    private static User USER3 = new User("User3", "user3@yandex.ru", UserFlag.active);
    private static List<User> FIST5_USERS = Arrays.asList(ADMIN, DELETED, FULL_NAME, USER1, USER2);

    public UserDaoTest() {
        super(UserDao.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        DBIProvider.getDBI().inTransaction((conn, status) -> {
            dao.insert(ADMIN);
            dao.insert(DELETED);
            dao.insert(FULL_NAME);
            dao.insert(USER1);
            dao.insert(USER2);
            dao.insert(USER3);
            return null;
        });
        LOG.info("-----------   End setUp ---------------\n");
    }

    @Test
    public void getWithLimit() {
        List<User> users = dao.getWithLimit(5);
        Assert.assertEquals(FIST5_USERS, users);
    }
}