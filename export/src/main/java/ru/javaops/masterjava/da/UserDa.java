package ru.javaops.masterjava.da;

import ru.javaops.masterjava.da.model.UserDaDto;

import java.util.Collection;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public interface UserDa {

    void saveUsers(Collection<UserDaDto> users);
}
