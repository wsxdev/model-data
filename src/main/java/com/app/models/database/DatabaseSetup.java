package com.app.models.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {

    public static void initializeTables() {
        String sqlMother = """
                CREATE TABLE IF NOT EXISTS madre (
                    id_madre SERIAL PRIMARY KEY,
                    identificacion VARCHAR(50) NOT NULL UNIQUE
                );
                """;

        String sqlBirth = """
                CREATE TABLE IF NOT EXISTS nacimiento (
                    id_nacimiento SERIAL PRIMARY KEY,
                    id_madre INT NOT NULL,
                    id_provincia VARCHAR(10) NOT NULL,
                    id_instruccion VARCHAR(10) NOT NULL,
                    fecha_nacimiento DATE NOT NULL,
                    anio INT NOT NULL,
                    FOREIGN KEY (id_madre) REFERENCES madre(id_madre),
                    FOREIGN KEY (id_provincia) REFERENCES provincias(id_provincia),
                    FOREIGN KEY (id_instruccion) REFERENCES instrucciones(id_instruccion)
                );
                """;

        DatabaseConfig config = new DatabaseConfig();
        DatabaseConnection connection = new DatabaseConnection(config);

        try (Connection conn = connection.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute(sqlMother);
            System.out.println("Table 'madre' verified.");

            stmt.execute(sqlBirth);
            System.out.println("Table 'nacimiento' verified.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Optional main to run it manually if needed, though we will call it from App
    // or DAO
    public static void main(String[] args) {
        initializeTables();
    }
}
