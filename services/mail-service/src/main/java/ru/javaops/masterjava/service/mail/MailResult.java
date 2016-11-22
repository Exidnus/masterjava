package ru.javaops.masterjava.service.mail;

/**
 * Created by dmitriy_varygin on 20.11.16.
 */
public class MailResult {
    private static final String OK = "OK";

    private final String email;
    private final String result;

    private static MailResult ok(String email) {
        return new MailResult(email, OK);
    }

    private static MailResult error(String email, String error) {
        return new MailResult(email, error);
    }

    public boolean isOk() {
        return OK.equals(result);
    }

    private MailResult(String email, String cause) {
        this.email = email;
        this.result = cause;
    }

    @Override
    public String toString() {
        return '(' + email + ',' + result + ')';
    }
}
