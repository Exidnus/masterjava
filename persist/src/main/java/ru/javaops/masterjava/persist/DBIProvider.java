package ru.javaops.masterjava.persist;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.SLF4JLog;
import org.skife.jdbi.v2.tweak.ConnectionFactory;
import org.slf4j.Logger;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * gkislin
 * 01.11.2016
 */
public class DBIProvider {
    private static final Logger LOG = getLogger(DBIProvider.class);
    private static String profile = "tomcat_pool";

    public static void setProfile(String profile) {
        DBIProvider.profile = profile;
    }

    private static class DBIHolder {
        static DBI jDBI = null;

        static {
            if (profile.equals("tomcat_pool")) {
                try {
                    InitialContext ctx = new InitialContext();
                    init(new DBI((DataSource) ctx.lookup("java:/comp/env/jdbc/masterjava")));
                } catch (Exception ex) {
                    throw new IllegalStateException("PostgreSQL initialization failed", ex);
                }
            }
        }

        static void init(DBI dbi) {
            LOG.info("Init jDBI with profile: " + profile);
            DBIHolder.jDBI = dbi;
            DBIHolder.jDBI.setSQLLog(new SLF4JLog());
        }
    }

    public static void init(ConnectionFactory connectionFactory) {
        DBIHolder.init(new DBI(connectionFactory));
    }

    public static DBI getDBI() {
        return DBIHolder.jDBI;
    }

    public static <T extends AbstractDao> T getDao(Class<T> daoClass) {
        return DBIHolder.jDBI.onDemand(daoClass);
    }
}
