package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IRole;
import com.app.models.database.DatabaseConfig;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleImpl implements IRole {

    private final DatabaseConnection databaseConnection;

    public RoleImpl() {
        this.databaseConnection = new DatabaseConnection(new DatabaseConfig());
    }

    @Override
    public Role findByName(String name) {
        String sql = "SELECT * FROM roles WHERE nombre_rol = ?";
        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Role(rs.getInt("id_rol"), rs.getString("nombre_rol"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Role create(Role role) {
        String sql = "INSERT INTO roles (nombre_rol) VALUES (?) RETURNING id_rol";
        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.getRoleName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    role.setIdRole(rs.getInt("id_rol"));
                    return role;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating role", e);
        }
        return null;
    }
}
