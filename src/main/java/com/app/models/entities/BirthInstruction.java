package com.app.models.entities;

public class BirthInstruction extends BirthRegister {

    // Attributes
    private int idBirth;
    private Instruction instruction;

    // Constructor
    public BirthInstruction(int idBirth, int year, Instruction instruction, int quantity) {
        super(year, quantity);
        this.idBirth = idBirth;
        this.instruction = instruction;
    }
    // Setters y Getters
    public int getIdBirth() {
        return idBirth;
    }
    public void setIdBirth(int idBirth) {
        this.idBirth = idBirth;
    }
    public Instruction getInstruction() {
        return instruction;
    }
    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    @Override
    public String toString() {
        return "BirthInstruction{" +
                "idBirth=" + idBirth +
                ", instruction=" + instruction.getNameInstruction() +
                ", " + super.toString() +
                '}';
    }
}
