package com.app.models.services;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record YearDataSummary(int year, Map<String, Integer> countByColumnBirth) {
    public YearDataSummary(int year, Map<String, Integer> countByColumnBirth) {
        this.year = year;
        this.countByColumnBirth = new LinkedHashMap<>(countByColumnBirth);
    }

    // OBTENER CANTIDAD DE REGISTROS
    public Map<String, Integer> countByRecord() {
        return Collections.unmodifiableMap(countByColumnBirth);
    }
    public int getCountForRecord(String provinceId) {
        // RETORNA UN ID POR DEFECTO
        return countByColumnBirth.getOrDefault(provinceId, 0);
    }

}
