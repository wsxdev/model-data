package com.app.models.entities;

public class Instruction {

    // Attributes
    private String idInstruction;
    private String nameInstruction;

    // Constructor
    public Instruction(String idInstruction, String nameInstruction) {
        this.idInstruction = idInstruction;
        this.nameInstruction = nameInstruction;
    }

    // Setters y Getters
    public String getIdInstruction() {
        return idInstruction;
    }
    public void setIdInstruction(String idInstruction) {
        this.idInstruction = idInstruction;
    }
    public String getNameInstruction() {
        return nameInstruction;
    }
    public void setNameInstruction(String nameInstruction) {
        this.nameInstruction = nameInstruction;
    }

}
