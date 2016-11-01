package ru.javaops.masterjava.persist.dao;

import org.junit.Before;
import ru.javaops.masterjava.persist.AbstractDao;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.DBITestProvider;

/**
 * gkislin
 * 27.10.2016
 */
public abstract class AbstractDaoTest<DAO extends AbstractDao> {
    static {
        DBITestProvider.initDBI();
    }

    protected DAO dao;

    protected AbstractDaoTest(Class<DAO> daoClass) {
        this.dao = DBIProvider.getDBI().onDemand(daoClass);
    }

    @Before
    public void setUp() throws Exception {
        dao.clean();
    }

}
