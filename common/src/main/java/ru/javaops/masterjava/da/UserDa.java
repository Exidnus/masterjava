package ru.javaops.masterjava.da;

import ru.javaops.masterjava.da.model.UserDaDto;

import java.util.Collection;
import java.util.List;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public interface UserDa {

    void saveUsers(Collection<UserDaDto> users);

    List<UserDaDto> getAllSorted();

    static UserDa getUserDaWithHardcodedDataSource() {
        return new UserDaJdbcTemplate(DataSourceSupplier.getPostgresDataSource());
    }

    static UserDa getUserDaWithDataSourceFromJndi() {
        return new UserDaJdbcTemplate(DataSourceSupplier.getJndiDataSource());
    }
}
