package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.AbstractDao;
import ru.javaops.masterjava.persist.model.Email;
import ru.javaops.masterjava.persist.model.SendEmailResult;

import java.util.List;

/**
 * Created by dmitriy_varygin on 21.11.16.
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class EmailDao implements AbstractDao {

    @Override
    @SqlUpdate("TRUNCATE emails CASCADE; TRUNCATE send_email_results CASCADE")
    public abstract void clean();

    @SqlUpdate("INSERT INTO emails (subject, message) VALUES (:subject, :message)")
    @GetGeneratedKeys
    public abstract int insertEmailGeneratedId(@BindBean Email email);

    @SqlBatch("INSERT INTO send_email_results (id, email_address, succeed, fail_cause, id_email) " +
            "VALUES (:id, :emailAddress, :succeed, :failCause, :emailId)")
    public abstract void insertSendEmailResults(@BindBean List<SendEmailResult> results);

    @SqlQuery("SELECT * FROM send_email_results")
    public abstract List<SendEmailResult> getAllEmailResults();
}
