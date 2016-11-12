package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.AbstractDao;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.UserGroup;

import java.util.List;

/**
 * Created by dmitriy_varygin on 06.11.16.
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

    @Override
    @SqlUpdate("TRUNCATE groups")
    public abstract void clean();

    @SqlUpdate("INSERT INTO groups (name, type, project_id) " +
            "VALUES (:name, CAST(:type AS group_type), :projectId)")
    public abstract void insertGeneratedId(@BindBean Group group);

    @SqlQuery("SELECT * FROM groups")
    public abstract List<Group> getAll();

    @SqlBatch("INSERT INTO groups (name, type, project_id) " +
            "VALUES (:name, CAST(:type AS group_type), :projectId)")
    public abstract void saveListWithoutSkippingSeq(@BindBean List<Group> groups);

    public void saveUserGroups(final List<UserGroup> userGroups) {
        DBIProvider.getDBI().inTransaction((handle, status) -> {
            final PreparedBatch preparedBatch = handle.prepareBatch("INSERT INTO users_groups VALUES (:1, :2)");
            userGroups.forEach(ug -> preparedBatch.add(ug.getUserId(), ug.getGroupId()));
            preparedBatch.execute();
            return null;
        });
    }
}
