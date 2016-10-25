package ru.javaops.masterjava.persist;

import ru.javaops.masterjava.model.User;

import java.util.Collection;
import java.util.List;

/**
 * Created by dmitriy_varygin on 25.10.16.
 */
public interface UserDao {

    void save(Collection<User> users);

    List<User> getAllSorted();

    static UserDao getUserDaoDefault() {
        return new UserDaoImpl(DataSourceSupplier.getPostgresDataSource());
    }
}
