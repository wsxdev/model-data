package com.app.models.modeling;

import com.app.models.entities.BirthRegister;

import java.util.List;

public abstract class GrowthModel {
    protected double initialConstant;
    protected double growthRate;

    public abstract void fit(List<BirthRegister> data);
    public abstract double evaluate(double time);

    public double getGrowthRate() { return growthRate; }
    public double getInitialConstant() { return initialConstant; }
}
