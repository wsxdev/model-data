package com.app.models.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseConnection {
    public Connection connection;

    public DatabaseConnection() {}

    public void getConnection() {
        try {
            Properties properties = new Properties();
            properties.load(DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties"));
            Class.forName(properties.getProperty("database.driver"));
            connection = DriverManager.getConnection(properties.getProperty("database.url"), properties.getProperty("database.user"), properties.getProperty("database.password"));
            System.out.println("The connection has been established");
        } catch (ClassNotFoundException | SQLException | IOException e) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
