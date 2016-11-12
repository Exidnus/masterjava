package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.AbstractDao;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.User;

import java.util.List;

/**
 * gkislin
 * 27.10.2016
 * <p>
 * <p>
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class UserDao implements AbstractDao {

    public User insert(User user) {
        if (user.isNew()) {
            int id = insertGeneratedId(user);
            user.setId(id);
        } else {
            insertWitId(user);
        }
        return user;
    }

    @SqlUpdate("INSERT INTO users (full_name, email, flag, city_id) " +
            "VALUES (:fullName, :email, CAST(:flag AS user_flag), :cityId)")
    @GetGeneratedKeys
    abstract int insertGeneratedId(@BindBean User user);

    @SqlUpdate("INSERT INTO users (id, full_name, email, flag, city_id) " +
            "VALUES (:id, :fullName, :email, CAST(:flag AS user_flag), :cityId) ")
    abstract void insertWitId(@BindBean User user);

    @SqlQuery("SELECT * FROM users ORDER BY full_name, email LIMIT :it")
    public abstract List<User> getWithLimit(@Bind int limit);

    //   http://stackoverflow.com/questions/13223820/postgresql-delete-all-content
    @SqlUpdate("TRUNCATE users CASCADE")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT nextval('common_seq')")
    abstract int getNextVal();

    @Transaction
    public int getSeqAndSkip(int step) {
        int id = getNextVal();
        DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE common_seq RESTART WITH " + (id + step)));
        return id;
    }

    public void saveChunk(List<User> users) {
        DBIProvider.getDBI().inTransaction((handle, status) -> {
            PreparedBatch preparedBatch =
                    handle.prepareBatch("INSERT INTO users (id, full_name, email, flag, city_id) " +
                            "VALUES (:1, :2, :3, CAST(:4 AS user_flag), :5)");
            users.forEach(u -> preparedBatch.add(u.getId(), u.getFullName(), u.getEmail(), u.getFlag(), u.getCityId()));
            preparedBatch.execute();
            return null;
        });
    }
}
