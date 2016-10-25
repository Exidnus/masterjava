package ru.javaops.masterjava.export;

import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import ru.javaops.masterjava.model.User;
import ru.javaops.masterjava.persist.UserDao;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Created by dmitriy_varygin on 25.10.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserExportTest {

    @Captor
    private ArgumentCaptor<Collection<User>> listUsersDaCaptor;
    private final UserDao userDa = Mockito.mock(UserDao.class);
    private final UserExport userServiceXml = new UserExport().withUserDaoOnlyForTest(userDa);

    @Test
    public void shouldExtractUserFromXmlAndCallDa() throws IOException, XMLStreamException {
        final InputStream is = Resources.getResource("payload.xml").openStream();
        userServiceXml.process(is);

        Mockito.verify(userDa, Mockito.atLeast(1)).save(listUsersDaCaptor.capture());

        Assert.assertEquals(listUsersDaCaptor.getValue().size(), 6);
    }
}