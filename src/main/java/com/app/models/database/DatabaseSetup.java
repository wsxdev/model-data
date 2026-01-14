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

        DatabaseConnection connection = DatabaseConnection.getInstance();

        try (Connection conn = connection.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute(sqlMother);
            System.out.println("Table 'madre' verified.");

            stmt.execute(sqlBirth);
            System.out.println("Table 'nacimiento' verified.");

            // SEED DATA
            checkAndCreateDefaultUser(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkAndCreateDefaultUser(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // 1. Insert roles if they don't exist
            stmt.execute("INSERT INTO roles (nombre_rol) VALUES ('ADMIN') ON CONFLICT (nombre_rol) DO NOTHING");
            stmt.execute("INSERT INTO roles (nombre_rol) VALUES ('OPERADOR') ON CONFLICT (nombre_rol) DO NOTHING");

            // 2. Check if admin user exists
            String checkSql = "SELECT COUNT(*) FROM usuarios WHERE nombre_usuario = 'admin'";
            try (java.sql.ResultSet rs = stmt.executeQuery(checkSql)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // Password 'admin123'
                    String hashedPassword = com.app.utils.PasswordUtil.hashPassword("admin123");
                    String insertUserSql = "INSERT INTO usuarios (nombre_usuario, password) VALUES ('admin', '"
                            + hashedPassword + "')";
                    stmt.execute(insertUserSql);

                    // 3. Assign ADMIN role
                    String assignRoleSql = """
                            INSERT INTO usuario_rol (id_usuario, id_rol)
                            SELECT u.id_usuario, r.id_rol
                            FROM usuarios u, roles r
                            WHERE u.nombre_usuario = 'admin' AND r.nombre_rol = 'ADMIN'
                            ON CONFLICT DO NOTHING
                            """;
                    stmt.execute(assignRoleSql);
                    System.out.println("Default user 'admin' created and configured.");
                } else {
                    System.out.println("Default user 'admin' already exists.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error seeding database: " + e.getMessage());
        }
    }

    // Optional main to run it manually if needed, though we will call it from App
    // or DAO
    public static void main(String[] args) {
        initializeTables();
    }
}
