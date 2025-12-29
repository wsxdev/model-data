package com.app.models.services;

import com.app.models.dao.implementations.BirthProvinceImpl;
import com.app.models.dao.implementations.ProvinceImpl;
import com.app.models.dao.interfaces.IBirthProvince;
import com.app.models.dao.interfaces.IProvince;
import com.app.models.entities.BirthProvince;
import com.app.models.entities.Province;

import java.util.*;

public class BirthService {
    // oa :)
    IBirthProvince birthProvinceDao = new BirthProvinceImpl();
    public List<YearProvinceSummary> getPivotByYear() {
        // GUARDA NACIMIENTOS - PROVINCIA
        List<BirthProvince> birthProvinces = birthProvinceDao.getBirthProvinces();

        // ORDENAR NOMBRES DE PROVINCIAS DE ACUERDO AL REGISTRO DE NACIMIENTOS POR PROVINCIA
        LinkedHashMap<String, String> provincesMap = new LinkedHashMap<>();
        for (BirthProvince birthProv : birthProvinces) {
            if (birthProv.getProvince() != null && birthProv.getProvince().getIdProvince() != null) {
                provincesMap.putIfAbsent(birthProv.getProvince().getIdProvince(), birthProv.getProvince().getNameProvince());
            }
        }

        // ORDENAR DESDE LA PROPIA CLASE DAO
        List<String> provinceIds = new ArrayList<>();
        if (provincesMap.isEmpty()) {
            IProvince provinceDao = new ProvinceImpl();

            // EVITAR BOILERPLATE :)
            /* List<Province> provinces = provinceDao.getProvinces();
            for (Province prov : provinces) {
                provinceIds.add(prov.getIdProvince());
            }*/
            for (Province prov : provinceDao.getProvinces()) provinceIds.add(prov.getIdProvince());
        } else {
            provinceIds.addAll(provincesMap.keySet());
        }

        // AÑOS ORDENADOS
        Set<Integer> years = new TreeSet<>();
        /*for (BirthProvince births : birthProvinces) {
            years.add(births.getYear());
        }*/
        for (BirthProvince births : birthProvinces) years.add(births.getYear());

        // INICIALIZAR ESTRUCTURA AÑOS DE ACUERDO CON LA PROVINCIA - CANTIDAD
        Map<Integer, Map<String, Integer>> matrix = new LinkedHashMap<>();

        for (Integer year : years) {
            Map<String, Integer> row = new LinkedHashMap<>();
            for (String provId : provinceIds) row.put(provId, 0);
            matrix.put(year, row);

            /* EJEMPLO DE LA MATRIX
            * 2000 -> {01=0, 02=0...}
            * 2001 -> {01=0, 02=0...}
            * 2002 -> {01=0, 02=0...}
            * */
        }

        // LLENAR MATRIX PARCIALMENTE
        for (BirthProvince birthProv : birthProvinces) {
            if (birthProv.getProvince() == null || birthProv.getProvince().getIdProvince() == null) continue;
            String provId = birthProv.getProvince().getIdProvince();
            int year =  birthProv.getYear();

            matrix.computeIfAbsent(year, k -> {
                Map<String, Integer> l = new LinkedHashMap<>(); // Help me! :(
                for (String id  : provinceIds) l.put(id, 0);
                return l;
            });

            // Arono :)
            Map<String, Integer> rowQuantity = matrix.get(year);
            rowQuantity.merge(provId, birthProv.getQuantity(),  Integer::sum);
        }

        List<YearProvinceSummary> result = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Integer>> entry : matrix.entrySet()) {
            result.add(new YearProvinceSummary(entry.getKey(), entry.getValue()));

        }

        return result;

    }

    public List<Province> getProvinceOrderBirths() {
        IBirthProvince birthProvinceDao = new BirthProvinceImpl();
        List<BirthProvince> birthProvinces = birthProvinceDao.getBirthProvinces();

        LinkedHashMap<String, String> provincesMap = new LinkedHashMap<>(); // Licet y yo estábamos programando. Se durmió, hora: 00:55 :)
        for (BirthProvince birthProv : birthProvinces) {
            if (birthProv.getProvince() != null && birthProv.getProvince().getIdProvince() != null) {
                provincesMap.putIfAbsent(birthProv.getProvince().getIdProvince(), birthProv.getProvince().getNameProvince());
            }
        }
        List<Province> resultProvinces = new ArrayList<>();
        if (!provincesMap.isEmpty()) {
            for (Map.Entry<String, String> entry : provincesMap.entrySet()) {
                resultProvinces.add(new Province(entry.getKey(), entry.getValue()));
            }
            return resultProvinces;
        }
        IProvince provinceDao = new ProvinceImpl();
        return provinceDao.getProvinces();
    }

}
