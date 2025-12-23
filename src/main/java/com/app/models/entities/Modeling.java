package com.app.models.entities;

public class Modeling {

    // Attributes
    private int id;
    private double finalPopulation;
    private double InitialPopulation;
    private double eulerConstant;
    private double changeConstant;
    private double time;

    // Constructor
    public Modeling(int id, double finalPopulation, double initialPopulation, double eulerConstant, double changeConstant, double time) {
        this.id = id;
        this.finalPopulation = finalPopulation;
        InitialPopulation = initialPopulation;
        this.eulerConstant = eulerConstant;
        this.changeConstant = changeConstant;
        this.time = time;
    }

    // Setters y Getters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public double getFinalPopulation() {
        return finalPopulation;
    }
    public void setFinalPopulation(double finalPopulation) {
        this.finalPopulation = finalPopulation;
    }
    public double getInitialPopulation() {
        return InitialPopulation;
    }
    public void setInitialPopulation(double initialPopulation) {
        InitialPopulation = initialPopulation;
    }
    public double getEulerConstant() {
        return eulerConstant;
    }
    public void setEulerConstant(double eulerConstant) {
        this.eulerConstant = eulerConstant;
    }
    public double getChangeConstant() {
        return changeConstant;
    }
    public void setChangeConstant(double changeConstant) {
        this.changeConstant = changeConstant;
    }
    public double getTime() {
        return time;
    }
    public void setTime(double time) {
        this.time = time;
    }

}