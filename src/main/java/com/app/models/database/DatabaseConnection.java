package com.app.models.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final HikariDataSource dataSource;

    private DatabaseConnection() {
        Properties properties = new Properties();
        try (InputStream inputProperties = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (inputProperties != null) {
                properties.load(inputProperties);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar database.properties", e);
        }

        HikariConfig configuration = new HikariConfig();
        configuration.setJdbcUrl(properties.getProperty("localDatabase.url"));
        configuration.setUsername(properties.getProperty("localDatabase.user"));
        configuration.setPassword(properties.getProperty("localDatabase.password"));

        String driver = properties.getProperty("localDatabase.driver");
        if (driver != null && !driver.isBlank()) {
            configuration.setDriverClassName(driver);
        }

        // POOL CONFIGURATION
        configuration.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.max", "10")));
        configuration
                .setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "30000")));
        configuration.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idleTimeout", "600000")));
        configuration
                .setLeakDetectionThreshold(Long.parseLong(properties.getProperty("db.pool.leakThreshold", "2000")));

        this.dataSource = new HikariDataSource(configuration);
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    // Legacy support if needed, but preferably remove
    public DataSource getDataSource() {
        return dataSource;
    }
}
