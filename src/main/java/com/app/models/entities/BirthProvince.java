package com.app.models.entities;

public class BirthProvince extends BirthRegister {

    // Attributes
    private int idBirth;
    private Province province;

    // Constructor
    public BirthProvince(int idBirth, int year, Province province, int quantity) {
        super(year, quantity);
        this.idBirth = idBirth;
        this.province = province;
    }

    // Setters y Getters
    public int getIdBirth() { return idBirth; }
    public void setIdBirth(int idBirth) {
        this.idBirth = idBirth;
    }
    public Province getProvince() {
        return province;
    }
    public void setProvince(Province province) {
        this.province = province;
    }

    @Override
    public String toString() {
        return "BirthProvince{" +
                "idBirth=" + idBirth +
                ", province=" + province.getNameProvince() +
                ", " + super.toString() +
                '}';
    }
}
