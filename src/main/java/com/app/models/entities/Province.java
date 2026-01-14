package com.app.models.entities;

public class Province {
    // Attributes
    private String idProvince;
    private String nameProvince;

    // Constructor
    public Province(String idProvince, String province) {
        this.idProvince = idProvince;
        this.nameProvince = province;
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

    @Override
    public String toString() {
        return nameProvince != null ? nameProvince : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Province province = (Province) o;
        return java.util.Objects.equals(idProvince, province.idProvince);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(idProvince);
    }
}
