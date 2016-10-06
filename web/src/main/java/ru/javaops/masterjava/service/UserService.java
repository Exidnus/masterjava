package ru.javaops.masterjava.service;

import ru.javaops.masterjava.da.model.UserDaDto;

import java.util.List;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public interface UserService {

    List<UserDaDto> getAllSorted();

    static UserService getUserService() {
        return new UserServiceImpl();
    }
}
