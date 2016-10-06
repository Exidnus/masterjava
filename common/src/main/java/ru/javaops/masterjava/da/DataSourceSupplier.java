package ru.javaops.masterjava.da;

import org.osjava.sj.naming.Jndi;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
class DataSourceSupplier {

    private DataSourceSupplier() {

    }

    private static class PostgresDataSourceHolder {
        private static DriverManagerDataSource dataSource;
        static {
            dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(""); //TODO
            dataSource.setUrl("");
            dataSource.setUsername("");
            dataSource.setPassword("");
        }
    }

    static DataSource getPostgresDataSource() {
        return PostgresDataSourceHolder.dataSource;
    }

    static DataSource getJndiDataSource() {
        DataSource dataSource;
        try {
            dataSource = new Jndi().lookupDataSource(""); //TODO
        } catch (NamingException e) {
            throw new RuntimeException("Can't find DataSource through JNDI", e);
        }
        return dataSource;
    }
}
