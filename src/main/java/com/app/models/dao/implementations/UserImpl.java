package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IUser;
import com.app.models.database.DatabaseConfig;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.Role;
import com.app.models.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserImpl implements IUser {

    private final DatabaseConnection databaseConnection;

    public UserImpl() {
        this.databaseConnection = new DatabaseConnection(new DatabaseConfig());
    }

    @Override
    public User create(User user) {
        String sqlUser = "INSERT INTO usuarios (nombre_usuario, password) VALUES (?, ?) RETURNING id_usuario, created_at";
        String sqlUserRole = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";

        try (Connection conn = databaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction

            int userId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(sqlUser)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("id_usuario");
                        user.setIdUser(userId);
                        user.setCreatedAt(rs.getTimestamp("created_at"));
                    }
                }
            }

            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                try (PreparedStatement stmtRole = conn.prepareStatement(sqlUserRole)) {
                    for (Role role : user.getRoles()) {
                        stmtRole.setInt(1, userId);
                        stmtRole.setInt(2, role.getIdRole());
                        stmtRole.addBatch();
                    }
                    stmtRole.executeBatch();
                }
            }

            conn.commit();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ?";
        User user = null;

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setIdUser(rs.getInt("id_usuario"));
                    user.setUsername(rs.getString("nombre_usuario"));
                    user.setPassword(rs.getString("password"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setLastLogin(rs.getTimestamp("last_login"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (user != null) {
            user.setRoles(getRolesForUser(user.getIdUser()));
        }
        return user;
    }

    private List<Role> getRolesForUser(int userId) {
        List<Role> roles = new ArrayList<>();
        String sql = """
                SELECT r.id_rol, r.nombre_rol
                FROM roles r
                JOIN usuario_rol ur ON r.id_rol = ur.id_rol
                WHERE ur.id_usuario = ?
                """;

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(new Role(rs.getInt("id_rol"), rs.getString("nombre_rol")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public void updateLastLogin(int idUser) {
        String sql = "UPDATE usuarios SET last_login = CURRENT_TIMESTAMP WHERE id_usuario = ?";
        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
