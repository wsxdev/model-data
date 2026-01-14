package com.app.models.modeling;

import com.app.models.entities.BirthRegister;

import java.util.ArrayList;
import java.util.List;

public class ExponentialGrowthModel extends GrowthModel {

    @Override
    public void fit (List<BirthRegister> data) {
        if (data.size() < 2) {

            if (!data.isEmpty()) {
                this.initialPopulation = data.getFirst().getQuantity();
            } else {
                this.initialPopulation = 0;
            }
            this.growthRate = 0.0;
        }
        List<BirthRegister> births = new ArrayList<>();
        List<Integer> time = new ArrayList<>();
        List<Double> growthRate = new ArrayList<>();


    }


    @Override
    public double evaluate (double time) {
        return initialPopulation * Math.exp(growthRate * time);
    }
}
