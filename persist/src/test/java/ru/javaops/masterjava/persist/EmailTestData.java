package ru.javaops.masterjava.persist;

import com.google.common.collect.ImmutableList;
import ru.javaops.masterjava.persist.dao.EmailDao;
import ru.javaops.masterjava.persist.model.Email;
import ru.javaops.masterjava.persist.model.SendEmailResult;

import java.util.List;

/**
 * Created by dmitriy_varygin on 21.11.16.
 */
public class EmailTestData {

    private EmailTestData() {

    }

    public static final Email EMAIL_1 = new Email("business", "email body");
    public static final Email EMAIL_2 = new Email("fun stuff", "email about funny stuff body");
    public static final SendEmailResult SEND_EMAIL_RESULT_1 = new SendEmailResult(
            33, "vasya@mail.ru", true, 45
    );
    public static final SendEmailResult SEND_EMAIL_RESULT_2 = new SendEmailResult(
            34, "semen@yandex.ru", false, "Failed to send", 45
    );
    public static final List<SendEmailResult> SEND_EMAIL_RESULTS = ImmutableList.of(
            SEND_EMAIL_RESULT_1, SEND_EMAIL_RESULT_2
    );

    public static void setUp() {
        EmailDao emailDao = DBIProvider.getDao(EmailDao.class);
        emailDao.clean();
        final int emailTwoId = emailDao.insertEmailGeneratedId(EMAIL_2);
        SEND_EMAIL_RESULT_1.setEmailId(emailTwoId);
        SEND_EMAIL_RESULT_2.setEmailId(emailTwoId);
    }
}
