package ru.javaops.masterjava.da;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.javaops.masterjava.da.model.UserDaDto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public class UserDaTest {

    private final UserDa userDa = UserDa.getUserDa();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceSupplier.getPostgresDataSource());

    @Test
    public void shouldSaveUsers() {

        final int previous = countAllInDB();
        final ImmutableSet<UserDaDto> users = ImmutableSet.of(
                new UserDaDto("Vasiliy", "vas@mail.ru", "Kiev"),
                new UserDaDto("Semen", "sem@mail.ru", "Moscow"),
                new UserDaDto("Dmitriy", "dm@mail.ru", "Moscow"),
                new UserDaDto("Peter", "peter@mail.ru", "Kiev")
        );

        userDa.saveUsers(users);

        assertEquals(previous + countAllInDB(), users.size());
    }

    @Test
    public void shouldGetAllSortedByFullNameAndCity() {
        final List<UserDaDto> allSorted = userDa.getAllSorted();
        assertEquals(countAllInDB(), allSorted.size());

        final List<UserDaDto> checkSort = allSorted.stream()
                .sorted(Comparator.comparing(UserDaDto::getFullName)
                        .thenComparing(UserDaDto::getCity))
                .collect(Collectors.toList());

        assertEquals(allSorted, checkSort);
    }

    private int countAllInDB() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }
}
