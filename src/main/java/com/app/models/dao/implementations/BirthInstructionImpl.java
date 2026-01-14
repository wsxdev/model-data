package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IBirthInstruction;
import com.app.models.dao.interfaces.IInstruction;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.BirthInstruction;
import com.app.models.entities.Instruction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BirthInstructionImpl implements IBirthInstruction {
    public List<BirthInstruction> getBirthInstruction() {

        String sql = """
                SELECT id_nacimiento, anio, id_instruccion, cantidad
                FROM nacimientos_instruccion
                ORDER BY nacimientos_instruccion""";

        DatabaseConnection connection = DatabaseConnection.getInstance();

        IInstruction instructionDao = new InstructionImpl();
        List<BirthInstruction> birthInstructions = new ArrayList<>();
        List<Instruction> instructions = instructionDao.getInstructions();

        try (Connection connectionInstruction = connection.getConnection();
                Statement statement = connectionInstruction.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            Map<String, Instruction> instructionsMap = new HashMap<>();
            for (Instruction instr : instructions) {
                if (instr != null && instr.getIdInstruction() != null) {
                    instructionsMap.put(instr.getIdInstruction(), instr);
                }
            }
            while (resultSet.next()) {
                int idBirth = resultSet.getInt("id_nacimiento");
                String idInstruction = resultSet.getString("id_instruccion");
                int year = resultSet.getInt("anio");
                int cantidad = resultSet.getInt("cantidad");

                Instruction instructionDb = null;
                if (idInstruction != null) {
                    instructionDb = instructionsMap.get(idInstruction);

                }
                BirthInstruction birthInstruction = new BirthInstruction(idBirth, year, instructionDb, cantidad);
                birthInstructions.add(birthInstruction);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return birthInstructions;
    }

    @Override
    public void saveOrUpdate(BirthInstruction birthInstruction) {
        String queryCheck = "SELECT id_nacimiento FROM nacimientos_instruccion WHERE anio = ? AND id_instruccion = ?";
        String queryUpdate = "UPDATE nacimientos_instruccion SET cantidad = ? WHERE id_nacimiento = ?";
        String queryInsert = "INSERT INTO nacimientos_instruccion (anio, id_instruccion, cantidad) VALUES (?, ?, ?)";

        DatabaseConnection connection = DatabaseConnection.getInstance();

        try (Connection conn = connection.getConnection();
                java.sql.PreparedStatement stmtCheck = conn.prepareStatement(queryCheck)) {

            stmtCheck.setInt(1, birthInstruction.getYear());
            stmtCheck.setString(2, birthInstruction.getInstruction().getIdInstruction());

            try (ResultSet rs = stmtCheck.executeQuery()) {
                if (rs.next()) {
                    // Update
                    int id = rs.getInt("id_nacimiento");
                    try (java.sql.PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate)) {
                        stmtUpdate.setInt(1, birthInstruction.getQuantity());
                        stmtUpdate.setInt(2, id);
                        stmtUpdate.executeUpdate();
                    }
                } else {
                    // Insert
                    try (java.sql.PreparedStatement stmtInsert = conn.prepareStatement(queryInsert)) {
                        stmtInsert.setInt(1, birthInstruction.getYear());
                        stmtInsert.setString(2, birthInstruction.getInstruction().getIdInstruction());
                        stmtInsert.setInt(3, birthInstruction.getQuantity());
                        stmtInsert.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving/updating birth instruction: " + e.getMessage(), e);
        }
    }
}
