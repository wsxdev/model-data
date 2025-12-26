package com.app.models.modeling;

import java.time.LocalDateTime;

public class ModelResult {
    private final GrowthModelType modelingType;
    private final double[] parameters;
    private final LocalDateTime generatedModelingDateTime;

    public ModelResult(GrowthModelType modelingType,  double[] parameters, LocalDateTime generatedModelingDateTime) {
        this.modelingType = modelingType;
        this.parameters = parameters;
        this.generatedModelingDateTime = generatedModelingDateTime;

    }
}
