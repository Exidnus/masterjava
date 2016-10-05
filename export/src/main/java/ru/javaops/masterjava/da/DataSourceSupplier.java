package ru.javaops.masterjava.da;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
class DataSourceSupplier {

    private DataSourceSupplier() {

    }

    private static class Holder {
        private static DriverManagerDataSource dataSource;
        static {
            dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(""); //TODO
            dataSource.setUrl("");
            dataSource.setUsername("");
            dataSource.setPassword("");
        }
    }

    public static DataSource getDataSource() {
        return Holder.dataSource;
    }
}
