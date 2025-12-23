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

    @Override
    public String toString() {
        return  "year: " + year + ", quantity: " + quantity;
    }

}