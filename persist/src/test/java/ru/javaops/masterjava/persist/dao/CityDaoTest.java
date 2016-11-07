package ru.javaops.masterjava.persist.dao;

import org.junit.Test;
import ru.javaops.masterjava.persist.model.City;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by dmitriy_varygin on 05.11.16.
 */
public class CityDaoTest extends AbstractDaoTest<CityDao> {

    private static final City MOSCOW = new City("msk", "Москва");
    private static final City LONDON = new City("lnd", "Лондон");
    private static final City SPB = new City("spb", "Санкт-Петербург");
    private static final City KIEV = new City("kiv", "Киев");

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dao.insertGeneratedId(SPB);
        dao.insertGeneratedId(KIEV);
    }

    public CityDaoTest() {
        super(CityDao.class);
    }

    @Test
    public void shouldInsertGeneratedId() throws Exception {
        dao.insertGeneratedId(MOSCOW);
    }

    @Test
    public void shouldGetAllIdStr() throws Exception {
        final Set<String> allIdStr = dao.getAllIdStr();
        assertNotNull(allIdStr);
        assertTrue(allIdStr.size() == 2);
        assertTrue(allIdStr.contains("kiv"));
        assertTrue(allIdStr.contains("spb"));
    }

    @Test
    public void shouldGetAll() throws Exception {
        final Set<City> all = dao.getAll();
        assertNotNull(all);
        assertTrue(all.size() == 2);
        final long count = all.stream()
                .filter(city -> "kiv".equals(city.getIdStr()) || "spb".equals(city.getIdStr()))
                .count();
        System.out.println(count);
        System.out.println(all);
        assertTrue(count == 2);
    }

    @Test
    public void shouldSaveListCities() throws Exception {
        dao.saveListWithoutSkippingSeq(Arrays.asList(MOSCOW, LONDON));
        final Set<City> all = dao.getAll();
        assertEquals(all.size(), 4);
        System.out.println(all);
    }
}