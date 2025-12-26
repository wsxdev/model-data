package com.app.models.modeling;

import com.app.models.entities.BirthRegister;

import java.util.List;

public class ExponentialGrowthModel extends GrowthModel {

    @Override
    public void fit (List<BirthRegister> data) { }

    @Override
    public double evaluate (double time) {
        return initialConstant * Math.exp(growthRate * time);
    }
}
