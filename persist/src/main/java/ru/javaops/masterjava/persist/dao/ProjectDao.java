package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.AbstractDao;
import ru.javaops.masterjava.persist.model.Project;

import java.util.List;

/**
 * Created by dmitriy_varygin on 06.11.16.
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao {

    @Override
    @SqlUpdate("TRUNCATE projects CASCADE")
    public abstract void clean();

    @SqlUpdate("INSERT INTO projects (id, name, description) VALUES (:id, :name, :description)")
    public abstract void insertWithId(@BindBean Project project);

    @SqlUpdate("INSERT INTO projects (name, description) VALUES (:name, :description)")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean Project project);

    @SqlQuery("SELECT * FROM projects")
    public abstract List<Project> getAll();
}
