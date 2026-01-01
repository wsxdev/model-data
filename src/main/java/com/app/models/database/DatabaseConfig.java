package com.app.models.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
	private final HikariDataSource dataSource;

	public DatabaseConfig() {
		Properties properties = new Properties();
		try (InputStream inputProperties = getClass().getClassLoader().getResourceAsStream("database.properties")) {
			if (inputProperties != null) {
				properties.load(inputProperties);
			}
		} catch (IOException e) {
			throw new RuntimeException("No se pudo cargar database.properties", e);
		}

        // CONFIGURACIÓN DE LA BASE DE DATOS CON HIKARI
		HikariConfig configuration = new HikariConfig();
		configuration.setJdbcUrl(properties.getProperty("localDatabase.url"));
		configuration.setUsername(properties.getProperty("localDatabase.user"));
		configuration.setPassword(properties.getProperty("localDatabase.password"));
		String driver = properties.getProperty("localDatabase.driver");
		if (driver != null && !driver.isBlank()) {
			configuration.setDriverClassName(driver);
		}

        // CONFIGURACIÓN DEL POOL DE CONEXIONES
		configuration.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.max", "1")));
		configuration.setConnectionTimeout(Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "30000")));
		configuration.setIdleTimeout(Long.parseLong(properties.getProperty("db.pool.idleTimeout", "600000")));
		configuration.setLeakDetectionThreshold(Long.parseLong(properties.getProperty("db.pool.leakThreshold", "2000")));

		this.dataSource = new HikariDataSource(configuration);
	}

    // MÉTODO PARA OBTENER LA CONEXIÓN A LA BASE DE DATOS
	public DataSource getDataSource() {
		return dataSource;
	}

    // MÉTODO PARA CERRAR EL POOL DE CONEXIONES
	public void closeConnection() {
		if (dataSource != null) {
			dataSource.close();
		}
	}
}
