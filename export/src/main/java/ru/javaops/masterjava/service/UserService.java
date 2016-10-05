package ru.javaops.masterjava.service;

import java.net.URL;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public interface UserService {

    void saveUsersFromXmlToBD(String projectName, URL source);
}
