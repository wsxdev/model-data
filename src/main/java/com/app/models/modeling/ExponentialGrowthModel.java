package com.app.models.modeling;

import java.util.ArrayList;
import java.util.List;

public class ExponentialGrowthModel extends GrowthModel {

    public void fit(List<Integer> time, List<Double> amount) {
        if (time.isEmpty() || amount.isEmpty() || time.size() != amount.size()) {
            this.initialPopulation = 0;
            this.growthRate = 0;
            return;
        }

        // Simple exponential regression: y = a * e^(rt)
        // ln(y) = ln(a) + rt
        // Linear regression of ln(y) against t
        // Y' = A' + B'X
        // Y' = ln(y), A' = ln(a), B' = r, X = t

        int n = time.size();
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumXX = 0;

        for (int i = 0; i < n; i++) {
            double x = time.get(i);
            double y = Math.log(amount.get(i)); // Natural log of population

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double denom = (n * sumXX - sumX * sumX);
        if (denom == 0) {
            this.initialPopulation = amount.get(0);
            this.growthRate = 0;
            return;
        }

        double r = (n * sumXY - sumX * sumY) / denom;
        double lnA = (sumY - r * sumX) / n;

        this.growthRate = r;
        this.initialPopulation = Math.exp(lnA);
    }

    @Override
    public double evaluate(double time) {
        return initialPopulation * Math.exp(growthRate * time);
    }
}
