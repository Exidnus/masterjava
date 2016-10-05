package ru.javaops.masterjava.da;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.javaops.masterjava.da.model.UserDaDto;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public class UserDaJdbcTemplateImpl implements UserDa {

    private final NamedParameterJdbcTemplate namedParameterTemplate;
    private final SimpleJdbcInsert insert;
    private final Function<UserDaDto, MapSqlParameterSource> userToMapSqlParameters = u -> new MapSqlParameterSource()
            .addValue("id", u.getId())
            .addValue("fullName", u.getFullName())
            .addValue("email", u.getEmail())
            .addValue("city", u.getCity());

    public UserDaJdbcTemplateImpl() {
        final DataSource dataSource = DataSourceSupplier.getDataSource();
        namedParameterTemplate = new NamedParameterJdbcTemplate(dataSource);
        insert = new SimpleJdbcInsert(dataSource).withTableName("users").usingGeneratedKeyColumns("id");
    }

    @Override
    public void saveUsers(Collection<UserDaDto> users) {

        final SqlParameterSource[] forInsert = users.stream()
                .filter(UserDaDto::isNew)
                .map(userToMapSqlParameters)
                .toArray(MapSqlParameterSource[]::new);
        insert.executeBatch(forInsert);

        final SqlParameterSource[] forUpdate = users.stream()
                .filter(UserDaDto::isNotNew)
                .map(userToMapSqlParameters)
                .toArray(MapSqlParameterSource[]::new);
        namedParameterTemplate.batchUpdate("UPDATE users SET full_name=:fullName, email=:email, city=:city, " +
                "WHERE id=:id", forUpdate);
    }
}
