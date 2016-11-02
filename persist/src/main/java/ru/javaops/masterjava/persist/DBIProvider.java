package ru.javaops.masterjava.persist;

import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.SLF4JLog;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * gkislin
 * 01.11.2016
 */
@Slf4j
public class DBIProvider {
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
            log.info("Init jDBI with profile: " + profile);
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
