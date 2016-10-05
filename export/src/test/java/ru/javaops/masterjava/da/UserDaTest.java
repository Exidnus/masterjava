package ru.javaops.masterjava.da;

import com.google.common.collect.ImmutableSet;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.javaops.masterjava.da.model.UserDaDto;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public class UserDaTest extends TestCase {

    private final UserDa userDa = new UserDaJdbcTemplateImpl();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceSupplier.getDataSource());

    @Before
    public void clearTable() {
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    public void shouldSaveUsers() {
        final ImmutableSet<UserDaDto> users = ImmutableSet.of(
                new UserDaDto("Vasiliy", "vas@mail.ru", "Kiev"),
                new UserDaDto(22, "Semen", "sem@mail.ru", "Moscow"),
                new UserDaDto("Dmitriy", "dm@mail.ru", "Moscow"),
                new UserDaDto(1, "Peter", "peter@mail.ru", "Kiev")
        );

        userDa.saveUsers(users);

        final int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        assertEquals(count, users.size());
    }
}
