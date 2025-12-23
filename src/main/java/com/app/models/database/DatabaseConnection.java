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
        logger.debug("Obteniendo conexion a la base de datos");
        return configuration.getDataSource().getConnection();
    }

}
