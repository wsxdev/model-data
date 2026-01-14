package com.app.models.services;

import com.app.models.modeling.ExponentialGrowthModel;
import com.app.models.modeling.GrowthModel;
import com.app.models.services.records.YearDataSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelingService {

    private final BirthService birthService;

    public ModelingService() {
        this.birthService = new BirthService();
    }

    // Example calculation: Fit total births over years
    public GrowthModel fitNationalModel() {
        // 1. Get Data
        List<YearDataSummary> data = birthService.getPivotByYear();

        // 2. Prepare Data for Model
        List<Integer> years = new ArrayList<>();
        List<Double> quantities = new ArrayList<>();

        for (YearDataSummary summary : data) {
            years.add(summary.year());
            // Sum all columns for the total of that year
            double total = summary.countByColumnBirth().values().stream().mapToDouble(Integer::doubleValue).sum();
            quantities.add(total);
        }

        // 3. Fit Model
        GrowthModel model = new ExponentialGrowthModel();
        model.fit(years, quantities);

        return model;
    }
}
