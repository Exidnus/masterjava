package ru.javaops.masterjava.persist.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.javaops.masterjava.persist.EmailTestData;
import ru.javaops.masterjava.persist.model.SendEmailResult;

import java.util.List;

import static ru.javaops.masterjava.persist.EmailTestData.EMAIL_1;
import static ru.javaops.masterjava.persist.EmailTestData.SEND_EMAIL_RESULTS;

/**
 * Created by dmitriy_varygin on 21.11.16.
 */
public class EmailDaoTest extends AbstractDaoTest<EmailDao>{

    public EmailDaoTest() {
        super(EmailDao.class);
    }

    @Before
    public void setUp() {
        EmailTestData.setUp();
    }

    @Test
    public void shouldSaveEmail() {
        final int emailId = dao.insertEmailGeneratedId(EMAIL_1);
        Assert.assertTrue(emailId > 100_000);
    }

    @Test
    public void shouldSaveSendEmailResult() {
        dao.insertSendEmailResults(SEND_EMAIL_RESULTS);
        final List<SendEmailResult> allEmailResultsFromBd = dao.getAllEmailResults();
        //Assert.assertEquals(SEND_EMAIL_RESULTS, allEmailResultsFromBd);
        Assert.assertTrue(allEmailResultsFromBd.size() == 2);
    }
}
