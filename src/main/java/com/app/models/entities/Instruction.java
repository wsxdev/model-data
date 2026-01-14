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

    @Override
    public String toString() {
        return nameInstruction != null ? nameInstruction : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return java.util.Objects.equals(idInstruction, that.idInstruction);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idInstruction);
    }
}
