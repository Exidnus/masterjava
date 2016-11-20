package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.List;

/**
 * gkislin
 * 15.11.2016
 */
@Slf4j
public class MailSender {
    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        Email email = new SimpleEmail();
        email.setHostName("smtp.yandex.ru");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator("dmitriy.varygin@yandex.ru", "KFDMkfdm007007"));
        email.setSSLOnConnect(true);
        try {
            email.setFrom("dmitriy.varygin@yandex.ru");

            email.setSubject(subject);
            email.setMsg(body);

            email.addTo(to.stream().map(Addressee::getEmail).toArray(String[]::new));
            email.addCc(cc.stream().map(Addressee::getEmail).toArray(String[]::new));

            email.send();
        } catch (EmailException e) {
            log.warn("Can't set parameters and send email.");
            throw new RuntimeException(e);
        }

        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled()?"\nbody=" + body:""));
    }
}
