package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IMother;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.Mother;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MotherImpl implements IMother {

    private final DatabaseConnection databaseConnection;

    public MotherImpl() {
        this.databaseConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Mother create(Mother mother) {
        String sql = "INSERT INTO madre (identificacion, nombres, edad, estado_civil) VALUES (?, ?, ?, ?) RETURNING id_madre";

        // First check if exists
        Mother existing = findByIdentification(mother.getIdentification());
        if (existing != null) {
            // OPTIONAL: Update existing mother details if provided?
            // For now, we return existing, assuming ID is immutable unique key.
            // If user wants to update, we'd need an update method.
            return existing;
        }

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mother.getIdentification());
            stmt.setString(2, mother.getNames());
            stmt.setInt(3, mother.getAge());
            stmt.setString(4, mother.getCivilStatus());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    mother.setIdMother(rs.getInt("id_madre"));
                    return mother;
                }
            }
        } catch (Exception e) {
            // If parallel requests happen, handle unique constraint viol smoothly
            Mother doubleCheck = findByIdentification(mother.getIdentification());
            if (doubleCheck != null)
                return doubleCheck;
            throw new RuntimeException("Error creating mother: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Mother findByIdentification(String identification) {
        String sql = "SELECT id_madre, identificacion, nombres, edad, estado_civil FROM madre WHERE identificacion = ?";

        try (Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identification);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Mother(
                            rs.getInt("id_madre"),
                            rs.getString("identificacion"),
                            rs.getString("nombres"),
                            rs.getInt("edad"),
                            rs.getString("estado_civil"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding mother by ID: " + e.getMessage(), e);
        }
        return null;
    }
}
