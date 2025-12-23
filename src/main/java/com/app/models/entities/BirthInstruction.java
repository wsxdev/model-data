package com.app.models.entities;

public class BirthInstruction {

    // Attributes
    private int idBirth;
    private int year;
    private Instruction idInstruction;
    private int quantity;

    // Constructor
    public BirthInstruction(int idBirth, int year, Instruction idInstruction, int quantity) {
        this.idBirth = idBirth;
        this.year = year;
        this.idInstruction = idInstruction;
        this.quantity = quantity;
    }
    // Setters y Getters
    public int getIdBirth() {
        return idBirth;
    }
    public void setIdBirth(int idBirth) {
        this.idBirth = idBirth;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public Instruction getIdInstruction() {
        return idInstruction;
    }
    public void setIdInstruction(Instruction idInstruction) {
        this.idInstruction = idInstruction;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
