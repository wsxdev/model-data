package com.app.models.services;

import com.app.models.dao.implementations.BirthInstructionImpl;
import com.app.models.dao.implementations.BirthProvinceImpl;
import com.app.models.dao.implementations.InstructionImpl;
import com.app.models.dao.implementations.ProvinceImpl;
import com.app.models.dao.interfaces.IBirthInstruction;
import com.app.models.dao.interfaces.IBirthProvince;
import com.app.models.dao.interfaces.IInstruction;
import com.app.models.dao.interfaces.IProvince;
import com.app.models.entities.BirthInstruction;
import com.app.models.entities.BirthProvince;
import com.app.models.entities.Instruction;
import com.app.models.entities.Province;
import com.app.models.services.records.YearDataSummary;

import java.util.*;

public class BirthService {
    // oa :)
    // ORDENAR DATOS POR PROVINCIA
    IBirthProvince birthProvinceDao = new BirthProvinceImpl();

    public List<YearDataSummary> getPivotByYear() {
        // GUARDA NACIMIENTOS - PROVINCE
        List<BirthProvince> birthProvinces = birthProvinceDao.getBirthProvinces();

        // ORDENAR NOMBRES DE PROVINCIAS DE ACUERDO AL REGISTRO DE NACIMIENTOS POR
        // PROVINCIA
        LinkedHashMap<String, String> provincesMap = new LinkedHashMap<>();
        for (BirthProvince birthProv : birthProvinces) {
            if (birthProv.getProvince() != null && birthProv.getProvince().getIdProvince() != null) {
                provincesMap.putIfAbsent(birthProv.getProvince().getIdProvince(),
                        birthProv.getProvince().getNameProvince());
            }
        }

        // ORDENAR DESDE LA PROPIA CLASE DAO
        List<String> provinceIds = new ArrayList<>();
        if (provincesMap.isEmpty()) {
            IProvince provinceDao = new ProvinceImpl();

            // EVITAR BOILERPLATE :)
            /*
             * List<Province> provinces = provinceDao.getProvinces();
             * for (Province prov : provinces) {
             * provinceIds.add(prov.getIdProvince());
             * }
             */
            for (Province prov : provinceDao.getProvinces())
                provinceIds.add(prov.getIdProvince());
        } else {
            provinceIds.addAll(provincesMap.keySet());
        }

        // AÑOS ORDENADOS
        Set<Integer> years = new TreeSet<>();
        /*
         * for (BirthProvince births : birthProvinces) {
         * years.add(births.getYear());
         * }
         */
        for (BirthProvince births : birthProvinces)
            years.add(births.getYear());

        // INICIALIZAR ESTRUCTURA AÑOS DE ACUERDO CON LA PROVINCIA - CANTIDAD
        Map<Integer, Map<String, Integer>> matrix = new LinkedHashMap<>();

        for (Integer year : years) {
            Map<String, Integer> row = new LinkedHashMap<>();
            for (String provId : provinceIds)
                row.put(provId, 0);
            matrix.put(year, row);

            /*
             * EJEMPLO DE LA MATRIX
             * 2000 -> {01=0, 02=0...}
             * 2001 -> {01=0, 02=0...}
             * 2002 -> {01=0, 02=0...}
             */
        }

        // LLENAR MATRIX PARCIALMENTE
        for (BirthProvince birthProv : birthProvinces) {
            if (birthProv.getProvince() == null || birthProv.getProvince().getIdProvince() == null)
                continue;
            String provId = birthProv.getProvince().getIdProvince();
            int year = birthProv.getYear();

            matrix.computeIfAbsent(year, k -> {
                Map<String, Integer> l = new LinkedHashMap<>(); // Help me! :(
                for (String id : provinceIds)
                    l.put(id, 0);
                return l;
            });

            // Arono :)
            Map<String, Integer> rowQuantity = matrix.get(year);
            rowQuantity.merge(provId, birthProv.getQuantity(), Integer::sum);
        }

        List<YearDataSummary> result = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Integer>> entry : matrix.entrySet()) {
            result.add(new YearDataSummary(entry.getKey(), entry.getValue()));

        }

        return result;

    }

    public List<Province> getProvinceOrderBirths() {
        IBirthProvince birthProvinceDao = new BirthProvinceImpl();
        List<BirthProvince> birthProvinces = birthProvinceDao.getBirthProvinces();

        LinkedHashMap<String, String> provincesMap = new LinkedHashMap<>(); // Licet y yo estábamos programando. Se
                                                                            // durmió, hora: 00:55 :)
        for (BirthProvince birthProv : birthProvinces) {
            if (birthProv.getProvince() != null && birthProv.getProvince().getIdProvince() != null) {
                provincesMap.putIfAbsent(birthProv.getProvince().getIdProvince(),
                        birthProv.getProvince().getNameProvince());
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

    // ORDENAR DATOS POR INSTRUCCION
    IBirthInstruction birthInstructionDao = new BirthInstructionImpl();

    public List<YearDataSummary> getPivotYearInstruction() {

        // GUARDAR NACIMIENTOS - INSTRUCTION
        List<BirthInstruction> birthInstructions = birthInstructionDao.getBirthInstruction();

        // ORDENAR NOMBRES DE INSTRUCCIONES DE ACUERDO AL REGISTRO DE NACIMIENTOS POR
        // INSTRUCCIÓN
        LinkedHashMap<String, String> instructionsMap = new LinkedHashMap<>();
        for (BirthInstruction birthInstr : birthInstructions) {
            if (birthInstr.getInstruction() != null && birthInstr.getInstruction().getIdInstruction() != null) {
                instructionsMap.putIfAbsent(birthInstr.getInstruction().getIdInstruction(),
                        birthInstr.getInstruction().getNameInstruction());
            }
        }

        // ORDENAR DESDE LA PROPIA CLASE DAO
        List<String> instructIds = new ArrayList<>();
        if (instructionsMap.isEmpty()) {
            IInstruction instructionDao = new InstructionImpl();

            for (Instruction instr : instructionDao.getInstructions())
                instructIds.add(instr.getIdInstruction());

        } else {
            instructIds.addAll(instructionsMap.keySet());
        }

        // AÑOS ORDENADOS
        Set<Integer> years = new TreeSet<>();

        for (BirthInstruction birthsI : birthInstructions)
            years.add(birthsI.getYear());

        // INICIALIZAR ESTRUCTURA AÑOS DE ACUERDO CON LA INSTRUCCIÓN - CANTIDAD
        Map<Integer, Map<String, Integer>> matrixInstruction = new LinkedHashMap<>();

        for (Integer year : years) {
            Map<String, Integer> row = new LinkedHashMap<>();
            for (String instructId : instructIds)
                row.put(instructId, 0);
            matrixInstruction.put(year, row);
        }

        // LLENAR LA MATRIX PARCIALMENTE
        for (BirthInstruction birthsI : birthInstructions) {
            if (birthsI.getInstruction() == null || birthsI.getInstruction().getIdInstruction() == null)
                continue;
            String instructId = birthsI.getInstruction().getIdInstruction();
            int year = birthsI.getYear();

            matrixInstruction.computeIfAbsent(year, k -> {
                Map<String, Integer> w = new LinkedHashMap<>();
                for (String id : instructIds)
                    w.put(id, 0);
                return w;
            });

            Map<String, Integer> rowQuantity = matrixInstruction.get(year);
            rowQuantity.merge(instructId, birthsI.getQuantity(), Integer::sum);

        }

        List<YearDataSummary> resultInstruction = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Integer>> entry : matrixInstruction.entrySet()) {
            resultInstruction.add(new YearDataSummary(entry.getKey(), entry.getValue()));
        }
        return resultInstruction;
    }

    public List<Instruction> getInstructionOrderBirths() {

        IBirthInstruction birthInstructionDao = new BirthInstructionImpl();
        List<BirthInstruction> birthInstructions = birthInstructionDao.getBirthInstruction();

        LinkedHashMap<String, String> instructionsMap = new LinkedHashMap<>();
        for (BirthInstruction birthsI : birthInstructions) {
            if (birthsI.getInstruction() != null || birthsI.getInstruction().getIdInstruction() != null) {
                instructionsMap.putIfAbsent(birthsI.getInstruction().getIdInstruction(),
                        birthsI.getInstruction().getNameInstruction());
            }

        }
        List<Instruction> resulInstructions = new ArrayList<>();
        if (!instructionsMap.isEmpty()) {
            for (Map.Entry<String, String> entry : instructionsMap.entrySet()) {
                resulInstructions.add(new Instruction(entry.getKey(), entry.getValue()));
            }
            return resulInstructions;
        }
        IInstruction instructionDao = new InstructionImpl();
        return instructionDao.getInstructions();

    }

    // NUEVA FUNCIONALIDAD: REGISTRO Y CONSOLIDACIÓN
    private com.app.models.dao.interfaces.IMother motherDao = new com.app.models.dao.implementations.MotherImpl();
    private com.app.models.dao.interfaces.IBirthRegistration birthRegistrationDao = new com.app.models.dao.implementations.BirthRegistrationImpl();

    /**
     * Registra un nacimiento individual. Verifica o crea la madre.
     */
    public void registerBirth(com.app.models.entities.Mother motherData,
            Province province,
            Instruction instruction,
            java.sql.Date birthDate,
            String sex,
            String birthType) {

        // 1. Gestionar Madre (Buscar o Crear)
        com.app.models.entities.Mother mother = motherDao.create(motherData);

        // 2. Crear Registro
        com.app.models.entities.BirthRegistration birth = new com.app.models.entities.BirthRegistration(
                mother, province, instruction, birthDate, sex, birthType);

        birthRegistrationDao.create(birth);
    }

    /**
     * Consolida los registros individuales de un año específico en la tabla
     * agregada de Provincias.
     */
    public void consolidateProvinces(int year) {
        List<com.app.models.entities.BirthRegistration> microData = birthRegistrationDao.findByYear(year);

        // Agrupar por ID Provincia -> Cantidad
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Province> provinceMap = new HashMap<>();

        for (com.app.models.entities.BirthRegistration br : microData) {
            if (br.getProvince() != null) {
                String id = br.getProvince().getIdProvince();
                counts.put(id, counts.getOrDefault(id, 0) + 1);
                provinceMap.putIfAbsent(id, br.getProvince());
            }
        }

        // Upsert en la tabla agregada
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String provId = entry.getKey();
            int qty = entry.getValue();
            Province prov = provinceMap.get(provId);

            BirthProvince bp = new BirthProvince(0, year, prov, qty); // ID 0 porque es auto-incremental/upsert
            birthProvinceDao.saveOrUpdate(bp);
        }
    }

    /**
     * Consolida los registros individuales de un año específico en la tabla
     * agregada de Instrucción.
     */
    public void consolidateInstructions(int year) {
        List<com.app.models.entities.BirthRegistration> microData = birthRegistrationDao.findByYear(year);

        // Agrupar por ID Instrucción la Cantidad
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Instruction> instructionMap = new HashMap<>();

        for (com.app.models.entities.BirthRegistration br : microData) {
            if (br.getInstruction() != null) {
                String id = br.getInstruction().getIdInstruction();
                counts.put(id, counts.getOrDefault(id, 0) + 1);
                instructionMap.putIfAbsent(id, br.getInstruction());
            }
        }

        // Upsert en la tabla agregada
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String instrId = entry.getKey();
            int qty = entry.getValue();
            Instruction instr = instructionMap.get(instrId);

            BirthInstruction bi = new BirthInstruction(0, year, instr, qty);
            birthInstructionDao.saveOrUpdate(bi);
        }
    }

    // SERVICIOS PARA CONTROLLERS (CATALOGOS & BUSQUEDAS)
    public List<Province> getAllProvinces() {
        IProvince provinceDao = new ProvinceImpl();
        return provinceDao.getProvinces();
    }

    public List<Instruction> getAllInstructions() {
        IInstruction instructionDao = new InstructionImpl();
        return instructionDao.getInstructions();
    }

    public com.app.models.entities.Mother findMotherByIdentification(String identification) {
        return motherDao.findByIdentification(identification);
    }
}
