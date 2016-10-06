package ru.javaops.masterjava.service;

import ru.javaops.masterjava.da.UserDa;
import ru.javaops.masterjava.da.model.UserDaDto;

import java.util.List;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
class UserServiceImpl implements UserService {

    private final UserDa userDa = UserDa.getUserDa();

    @Override
    public List<UserDaDto> getAllSorted() {
        return userDa.getAllSorted();
    }
}
