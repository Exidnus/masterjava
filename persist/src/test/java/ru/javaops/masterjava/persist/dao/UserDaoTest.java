package ru.javaops.masterjava.persist.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;

import java.util.Arrays;
import java.util.List;

/**
 * gkislin
 * 27.10.2016
 */
@Slf4j
public class UserDaoTest extends AbstractDaoTest<UserDao> {

    private static User ADMIN = new User("Admin", "admin@javaops.ru", UserFlag.superuser, 1);
    private static User DELETED = new User("Deleted", "deleted@yandex.ru", UserFlag.deleted, 1);
    private static User FULL_NAME = new User("Full Name", "gmail@gmail.com", UserFlag.active, 1);
    private static User USER1 = new User("User1", "user1@gmail.com", UserFlag.active, 1);
    private static User USER2 = new User("User2", "user2@yandex.ru", UserFlag.active, 1);
    private static User USER3 = new User("User3", "user3@yandex.ru", UserFlag.active, 1);
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
        log.info("-----------   End setUp ---------------\n");
    }

    @Test
    public void getWithLimit() {
        List<User> users = dao.getWithLimit(5);
        Assert.assertEquals(FIST5_USERS, users);
    }

    @Test
    public void getSeqAndSkip() throws Exception {
        int seq1 = dao.getSeqAndSkip(5);
        int seq2 = dao.getSeqAndSkip(1);
        Assert.assertEquals(5, seq2 - seq1);
    }

    @Test
    public void saveChunk() throws Exception {
        dao.clean();
        dao.saveChunk(FIST5_USERS);
        List<User> users = dao.getWithLimit(100);
        Assert.assertEquals(FIST5_USERS, users);
    }
}