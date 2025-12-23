package com.app.models.entities;

public class Province {
    // Attributes
    private String idProvince;
    private String nameProvince;

    // Constructor
    public Province(String id_provincia, String provincia) {
        this.idProvince = id_provincia;
        this.nameProvince = provincia;
    }
    // Setters y Getters
    public String getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(String idProvince) {
        this.idProvince = idProvince;
    }

    public String getNameProvince() {
        return nameProvince;
    }

    public void setNameProvince(String nameProvince) {
        this.nameProvince = nameProvince;
    }
}
