package ru.javaops.masterjava.service;

import java.net.URL;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public interface UserServiceXml {

    void saveUsersFromXmlToBD(String projectName, URL source);

    static UserServiceXml getUserServiceXml() {
        return new UserServiceXmlImpl();
    }
}
