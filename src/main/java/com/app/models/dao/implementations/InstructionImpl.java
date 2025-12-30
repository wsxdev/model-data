package com.app.models.dao.implementations;

import com.app.models.dao.interfaces.IInstruction;
import com.app.models.database.DatabaseConfig;
import com.app.models.database.DatabaseConnection;
import com.app.models.entities.Instruction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class InstructionImpl implements IInstruction {
    public List<Instruction> getInstructions(){
         String sql = """
         SELECT id_instruccion, instruccion
         FROM instrucciones
         ORDER BY instrucciones""";

        DatabaseConfig config = new DatabaseConfig();
        DatabaseConnection databaseConnection = new DatabaseConnection(config);
        List<Instruction> instructions = new ArrayList<>();

        try(Connection connectionInstruction = databaseConnection.getConnection();
            Statement statement = connectionInstruction.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String idInstruction = resultSet.getString("id_instruccion");
                String nameInstruccion = resultSet.getString("instruccion");
                Instruction instruction = new Instruction(idInstruction, nameInstruccion);
                instructions.add(instruction);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return instructions;
    }
}
