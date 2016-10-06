package ru.javaops.masterjava.da;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import ru.javaops.masterjava.da.model.UserDaDto;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
class UserDaJdbcTemplate implements UserDa {

    private final SimpleJdbcInsert insert;
    private final JdbcTemplate template;

    UserDaJdbcTemplate(final DataSource dataSource) {
        insert = new SimpleJdbcInsert(dataSource).withTableName("users").usingGeneratedKeyColumns("id");
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public void saveUsers(Collection<UserDaDto> users) {
        final Function<UserDaDto, MapSqlParameterSource> userToMapSqlParameters = u -> new MapSqlParameterSource()
                .addValue("id", u.getId())
                .addValue("fullName", u.getFullName())
                .addValue("email", u.getEmail())
                .addValue("city", u.getCity());

        final SqlParameterSource[] forInsert = users.stream()
                .map(userToMapSqlParameters)
                .toArray(MapSqlParameterSource[]::new);
        insert.executeBatch(forInsert);
    }

    @Override
    public List<UserDaDto> getAllSorted() {
        final BeanPropertyRowMapper<UserDaDto> rowMapper = BeanPropertyRowMapper.newInstance(UserDaDto.class);
        return template.query("SELECT * FROM users ORDER BY full_name, city", rowMapper);
    }
}
