package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IBirthInstruction;
import com.app.models.dao.interfaces.IInstruction;
import com.app.models.database.DatabaseConfig;
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

    public List<BirthInstruction> getBirthInstruction (){

        String sql = """
        SELECT id_nacimiento, anio, id_instruccion, cantidad
        FROM nacimientos_instruccion
        ORDER BY nacimientos_instruccion""";

        DatabaseConfig config = new DatabaseConfig();
        DatabaseConnection connection = new DatabaseConnection(config);

        IInstruction instructionDao = new InstructionImpl();
        List<BirthInstruction> birthInstructions = new ArrayList<>();
        List<Instruction> instructions = instructionDao.getInstructions();

        try (Connection connectionInstruction = connection.getConnection();
             Statement statement = connectionInstruction.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)){

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

            int i = 0;
            while (i < birthInstructions.size()) {
                System.out.print(birthInstructions.get(i).getIdBirth());
                System.out.print(" " + birthInstructions.get(i).getInstruction().getIdInstruction());
                System.out.print(" " + birthInstructions.get(i).getInstruction().getNameInstruction());
                System.out.print(" " + birthInstructions.get(i).getYear());
                System.out.println(" " + birthInstructions.get(i).getQuantity());
                i++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return birthInstructions;

    }
}
