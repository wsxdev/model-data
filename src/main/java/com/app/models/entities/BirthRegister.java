package com.app.models.entities;

public abstract class BirthRegister {

    // Attributes
    protected int year;
    protected int quantity;

    // Constructor
    public BirthRegister(int year, int quantity) {
        this.year = year;
        this.quantity = quantity;
    }

    // GETTERS
    public int getYear() { return year; }
    public int getQuantity() { return quantity; }
    @Override
    public String toString() {
        return  "year: " + year + ", quantity: " + quantity;
    }

}