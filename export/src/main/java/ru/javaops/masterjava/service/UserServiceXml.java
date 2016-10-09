package ru.javaops.masterjava.service;

import java.io.InputStream;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public interface UserServiceXml {

    void saveUsersFromXmlToBD(InputStream is);

    static UserServiceXml getUserServiceXml() {
        return new UserServiceXmlImpl();
    }
}
