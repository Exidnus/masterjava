package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.javaops.masterjava.model.User;
import ru.javaops.masterjava.model.UserFlag;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by dmitriy_varygin on 25.10.16.
 */
public class UserDaoTest {

    private final UserDao userDa = UserDao.getUserDaoDefault();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceSupplier.getPostgresDataSource());

    @Test
    public void shouldSave() throws Exception {
        final ImmutableSet<User> users = ImmutableSet.of(
                new User("Vasiliy", "vas@mail.ru", UserFlag.active),
                new User("Semen", "sem@mail.ru", UserFlag.deleted),
                new User("Dmitriy", "dm@mail.ru", UserFlag.active),
                new User("Peter", "peter@mail.ru", UserFlag.superuser)
        );

        final int previous = countAllInDB();
        userDa.save(users);
        assertEquals(countAllInDB(), users.size() + previous);
    }

    @Test
    public void shouldGetAllSorted() throws Exception {
        final List<User> allSorted = userDa.getAllSorted();
        assertEquals(countAllInDB(), allSorted.size());

        final List<User> checkSort = allSorted.stream()
                .sorted(Comparator.comparing(User::getFullName))
                .collect(Collectors.toList());

        assertEquals(allSorted, checkSort);
    }

    private int countAllInDB() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }
}