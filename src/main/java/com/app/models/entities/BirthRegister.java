package com.app.models.entities;

public abstract class RegistroNatalidad {

    // Attributes
    private int year;
    private int quantity;

    // Constructor
    public RegistroNatalidad(int year, int quantity) {
        this.year = year;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return  " year: " + year + " quantity: " + quantity ;
    }

}