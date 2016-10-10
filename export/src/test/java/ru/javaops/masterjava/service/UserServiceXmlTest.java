package ru.javaops.masterjava.service;

import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import ru.javaops.masterjava.da.UserDa;
import ru.javaops.masterjava.da.model.UserDaDto;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceXmlTest {

    @Captor
    private ArgumentCaptor<Collection<UserDaDto>> listUsersDaCaptor;
    private final UserDa userDa = Mockito.mock(UserDa.class);
    private final UserServiceXml userServiceXml = new UserServiceXmlImpl().withUserDa(userDa);

    @Test
    public void shouldExtractUserFromXmlAndCallDa() throws IOException {
        final InputStream is = Resources.getResource("payload.xml").openStream();
        userServiceXml.saveUsersFromXmlToBD(is);

        Mockito.verify(userDa, Mockito.atLeast(1)).saveUsers(listUsersDaCaptor.capture());

        Assert.assertEquals(listUsersDaCaptor.getValue().size(), 6);
    }
}
