package br.com.connect.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MariaDBContainer;

public class DatabaseContainerConfiguration extends MariaDBContainer<DatabaseContainerConfiguration> {

    private static final String IMAGE_VERSION = "mariadb:10.6";
    private static DatabaseContainerConfiguration container;

    private DatabaseContainerConfiguration() {
        super(IMAGE_VERSION);
    }

    public static DatabaseContainerConfiguration getInstance() {
        if (container == null) {
            container = new DatabaseContainerConfiguration();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
    }
}
