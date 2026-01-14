package com.app.models.entities;

public class Mother {
    private int idMother;
    // We keep identification simple as per requirements (no demographic analysis)
    // could be a string like "CEDULA" or generated.
    // Requirement says: "Identificación básica de madre (sin análisis)"
    private String identification;
    private String names;
    private int age;
    private String civilStatus;

    public Mother(int idMother, String identification, String names, int age, String civilStatus) {
        this.idMother = idMother;
        this.identification = identification;
        this.names = names;
        this.age = age;
        this.civilStatus = civilStatus;
    }

    public Mother(String identification, String names, int age, String civilStatus) {
        this.identification = identification;
        this.names = names;
        this.age = age;
        this.civilStatus = civilStatus;
    }

    public Mother(String identification) {
        this.identification = identification;
    }

    public int getIdMother() {
        return idMother;
    }

    public void setIdMother(int idMother) {
        this.idMother = idMother;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    @Override
    public String toString() {
        return "Mother{" +
                "idMother=" + idMother +
                ", identification='" + identification + '\'' +
                '}';
    }
}
