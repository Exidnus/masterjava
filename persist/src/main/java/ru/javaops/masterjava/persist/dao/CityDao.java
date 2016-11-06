package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.AbstractDao;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;
import java.util.Set;

/**
 * @author Varygin DV
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao {

    @SqlUpdate("TRUNCATE cities")
    @Override
    public abstract void clean();

    @SqlQuery("SELECT * FROM cities")
    public abstract Set<City> getAll();

    @SqlQuery("SELECT cities.id_str FROM cities")
    public abstract Set<String> getAllIdStr();

    @SqlUpdate("INSERT INTO cities (id_str, name) VALUES (:idStr, :name)")
    public abstract void insertGeneratedId(@BindBean City city);
}