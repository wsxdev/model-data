package com.app.models.modeling;

import java.util.List;

public abstract class GrowthModel {
    protected double initialPopulation;
    protected double growthRate;

    public abstract void fit(List<Integer> time, List<Double> amount);

    public abstract double evaluate(double time);

    public double getGrowthRate() {
        return growthRate;
    }

    public double getInitialConstant() {
        return initialPopulation;
    }

}
