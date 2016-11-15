package ru.javaops.masterjava.persist.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import ru.javaops.masterjava.persist.AbstractDao;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.DBITestProvider;

/**
 * gkislin
 * 27.10.2016
 */
@Slf4j
public abstract class AbstractDaoTest<DAO extends AbstractDao> {
    static {
        DBITestProvider.initDBI();
    }

    @Rule
    public TestRule testWatcher = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            log.info("\n\n+++ Start " + description.getDisplayName());
        }

        @Override
        protected void finished(Description description) {
            log.info("\n+++ Finish " + description.getDisplayName() + '\n');
        }
    };

    protected DAO dao;

    protected AbstractDaoTest(Class<DAO> daoClass) {
        this.dao = DBIProvider.getDao(daoClass);
    }

}
