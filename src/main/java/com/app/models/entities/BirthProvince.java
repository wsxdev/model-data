package com.app.models.entities;

public class BirthProvince {

    // Attributes
    private int idBirth;
    private int year;
    private Province idProvince;
    private int quantity;

    // Constructor
    public BirthProvince(int idBirth, int year, Province idProvince, int quantity) {
        this.idBirth = idBirth;
        this.year = year;
        this.idProvince = idProvince;
        this.quantity = quantity;
    }

    // Setters y Getters
    public int getIdBirth() { return idBirth; }
    public void setIdBirth(int idBirth) {
        this.idBirth = idBirth;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public Province getIdProvince() {
        return idProvince;
    }
    public void setIdProvince(Province idProvince) {
        this.idProvince = idProvince;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
