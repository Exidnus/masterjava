package ru.javaops.masterjava.persist;

import org.osjava.sj.naming.Jndi;
import org.postgresql.ds.PGSimpleDataSource;

import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Created by dmitriy_varygin on 25.10.16.
 */
class DataSourceSupplier {

    private DataSourceSupplier() {

    }

    private static class PostgresDataSourceHolder {
        private static PGSimpleDataSource dataSource;
        static {
            dataSource = new PGSimpleDataSource();
            dataSource.setUrl("jdbc:postgresql://localhost:5432/masterjava");
            dataSource.setUser("user");
            dataSource.setPassword("password");
        }
    }

    static DataSource getPostgresDataSource() {
        return PostgresDataSourceHolder.dataSource;
    }

    static DataSource getJndiDataSource() {
        DataSource dataSource;
        try {
            dataSource = new Jndi().lookupDataSource("jdbc/masterjava");
        } catch (NamingException e) {
            throw new RuntimeException("Can't find DataSource through JNDI", e);
        }
        return dataSource;
    }
}
