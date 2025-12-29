package com.app.models.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class YearProvinceSummary {
    private final int year;
    private final Map<String, Integer> countsByProvince;
    private final Map<String, Integer> countsByInstruction;
    public YearProvinceSummary(int year, Map<String, Integer> countsByProvince) {
        this.year = year;
        this.countsByProvince = new LinkedHashMap<>(countsByProvince);
        this.countsByInstruction = new LinkedHashMap<>();
    }

    public YearProvinceSummary( Map<String, Integer> countsByInstruction) {
        this(countsByInstruction, year );
        this.countsByProvince = new LinkedHashMap<>();
    }

    public int getYear() { return year; }
    // OBTENER CANTIDAD DE PROVINCIAS
    public Map<String, Integer> getCountsByProvince() {
        return Collections.unmodifiableMap(countsByProvince);
    }

    public int getCountForProvince(String provinceId) {
        // RETORNA UN ID POR DEFECTO
        return countsByProvince.getOrDefault(provinceId, 0);
    }

}
