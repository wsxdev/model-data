package com.app.models.entities;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Represents a single birth registration (microdata).
 */
public class BirthRegistration {
    private int idBirthRegistration;
    private Mother mother;
    private Province province;
    private Instruction instruction;
    private Date birthDate;
    private int year; // Derived
    private String sex;
    private String birthType;

    public BirthRegistration(int idBirthRegistration, Mother mother, Province province, Instruction instruction,
            Date birthDate, String sex, String birthType) {
        this.idBirthRegistration = idBirthRegistration;
        this.mother = mother;
        this.province = province;
        this.instruction = instruction;
        this.birthDate = birthDate;
        this.sex = sex;
        this.birthType = birthType;
        this.year = calculateYear();
    }

    public BirthRegistration(Mother mother, Province province, Instruction instruction, Date birthDate, String sex,
            String birthType) {
        this.mother = mother;
        this.province = province;
        this.instruction = instruction;
        this.birthDate = birthDate;
        this.sex = sex;
        this.birthType = birthType;
        this.year = calculateYear();
    }

    // Keep legacy constructor for backward compatibility if needed, or update call
    // sites
    public BirthRegistration(int idBirthRegistration, Mother mother, Province province, Instruction instruction,
            Date birthDate) {
        this(idBirthRegistration, mother, province, instruction, birthDate, null, null);
    }

    public BirthRegistration(Mother mother, Province province, Instruction instruction, Date birthDate) {
        this(mother, province, instruction, birthDate, null, null);
    }

    private int calculateYear() {
        if (birthDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthDate);
            return calendar.get(Calendar.YEAR);
        }
        return 0;
    }

    public int getIdBirthRegistration() {
        return idBirthRegistration;
    }

    public void setIdBirthRegistration(int idBirthRegistration) {
        this.idBirthRegistration = idBirthRegistration;
    }

    public Mother getMother() {
        return mother;
    }

    public void setMother(Mother mother) {
        this.mother = mother;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        this.year = calculateYear();
    }

    public int getYear() {
        return year;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthType() {
        return birthType;
    }

    public void setBirthType(String birthType) {
        this.birthType = birthType;
    }

    @Override
    public String toString() {
        return "BirthRegistration{" +
                "idBirthRegistration=" + idBirthRegistration +
                ", mother=" + (mother != null ? mother.getIdentification() : "null") +
                ", province=" + (province != null ? province.getNameProvince() : "null") +
                ", instruction=" + (instruction != null ? instruction.getNameInstruction() : "null") +
                ", birthDate=" + birthDate +
                ", year=" + year +
                '}';
    }
}
