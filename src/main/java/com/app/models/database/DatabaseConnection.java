package com.app.models.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private final DatabaseConfig configuration;

    public DatabaseConnection(DatabaseConfig configuration) {
        this.configuration = configuration;
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection connection = configuration.getDataSource().getConnection();
            logger.info("The connection has been established");
            return connection;
        } catch (SQLException e) {
            logger.error("Error establishing connection", e.getMessage());
            throw e;
        }
    }

}
