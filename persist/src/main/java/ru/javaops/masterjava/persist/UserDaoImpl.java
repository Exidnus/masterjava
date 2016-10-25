package ru.javaops.masterjava.persist;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import ru.javaops.masterjava.model.User;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dmitriy_varygin on 25.10.16.
 */
class UserDaoImpl implements UserDao {

    private final SimpleJdbcInsert insert;
    private final JdbcTemplate template;

    UserDaoImpl(final DataSource dataSource) {
        insert = new SimpleJdbcInsert(dataSource).withTableName("users").usingGeneratedKeyColumns("id");
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public void save(Collection<User> users) {
        final Function<User, MapSqlParameterSource> userToMapSqlParameters = u -> new MapSqlParameterSource()
                .addValue("id", u.getId())
                .addValue("full_name", u.getFullName())
                .addValue("email", u.getEmail());

        final SqlParameterSource[] forInsert = users.stream()
                .map(userToMapSqlParameters)
                .toArray(MapSqlParameterSource[]::new);
        insert.executeBatch(forInsert);
    }

    @Override
    public List<User> getAllSorted() {
        final BeanPropertyRowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
        return template.query("SELECT * FROM users ORDER BY full_name", rowMapper);
    }
}
