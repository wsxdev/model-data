package com.app.models.services;

import com.app.models.modeling.*;
import com.app.models.entities.Province;
import com.app.models.entities.Instruction;
import com.app.models.services.records.YearDataSummary;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para ejecutar modelado matemático mediante ecuaciones diferenciales
 * ordinarias (EDO).
 * Implementa el modelo: dN/dt = B·N(t) → N(t) = A·e^(B·t)
 */
public class ModelingService {

    private final BirthService birthService;

    public ModelingService() {
        this.birthService = new BirthService();
    }

    /**
     * Ejecuta el modelo EDO sobre una serie temporal específica.
     *
     * @param tipo       Tipo de segmentación (PROVINCIA o INSTRUCCION)
     * @param categoria  Nombre de la categoría (ej: "Guayas", "Educación Básica")
     * @param anioInicio Año inicial del rango temporal
     * @param anioFin    Año final del rango temporal
     * @param intervalo  Intervalo entre años (ej: 1 = todos los años, 2 = cada 2
     *                   años)
     * @return ResultadoModeladoEDO con parámetros estimados, métricas y series
     *         temporales
     */
    public ResultadoModeladoEDO ejecutarModeladoEDO(
            TipoSegmentacion tipo,
            String categoria,
            int anioInicio,
            int anioFin,
            int intervalo) {

        // Obtener datos históricos según el tipo de segmentación
        List<PuntoTemporal> datosObservados = obtenerDatosHistoricos(tipo, categoria);

        // Filtrar por rango temporal e intervalo
        datosObservados = filtrarPorRangoTemporal(datosObservados, anioInicio, anioFin, intervalo);

        // Validar que haya suficientes datos
        if (datosObservados.size() < 2) {
            throw new RuntimeException("No hay suficientes datos para realizar el modelado (mínimo 2 puntos)");
        }

        // Filtrar valores válidos (N > 0 para aplicar logaritmo)
        List<PuntoTemporal> datosValidos = datosObservados.stream()
                .filter(p -> p.nacimientos() > 0)
                .collect(Collectors.toList());

        if (datosValidos.size() < 2) {
            throw new RuntimeException("No hay suficientes datos positivos para el modelado logarítmico");
        }

        // Estimar parámetros A y B mediante regresión lineal
        double[] parametros = estimarParametrosEDO(datosValidos);
        double A = parametros[0];
        double B = parametros[1];

        // Generar serie modelada N(t) = A * e^(B*t) (Puntos discretos coincidientes)
        List<PuntoTemporal> datosModelados = generarSerieModelada(datosObservados, A, B);

        // Generar serie modelada suave para graficación (Intervalos de 0.1 años)
        List<PuntoTemporal> modeladosCurve = generarSerieModeladaSuave(anioInicio, anioFin, A, B);

        // Calcular métricas de ajuste
        double r2 = calcularR2(datosValidos, datosModelados);
        double mae = calcularMAE(datosValidos, datosModelados);
        double rmse = calcularRMSE(datosValidos, datosModelados);

        return new ResultadoModeladoEDO(
                categoria,
                tipo,
                A,
                B,
                r2,
                mae,
                rmse,
                datosObservados,
                datosModelados,
                modeladosCurve);
    }

    /**
     * Obtiene los datos históricos de nacimientos según el tipo de segmentación.
     */
    private List<PuntoTemporal> obtenerDatosHistoricos(TipoSegmentacion tipo, String categoria) {
        if (tipo == TipoSegmentacion.PROVINCIA) {
            return obtenerDatosProvincia(categoria);
        } else if (tipo == TipoSegmentacion.INSTRUCCION) {
            return obtenerDatosInstruccion(categoria);
        }
        throw new IllegalArgumentException("Tipo de segmentación no soportado: " + tipo);
    }

    /**
     * Obtiene datos históricos para una provincia específica.
     */
    private List<PuntoTemporal> obtenerDatosProvincia(String nombreProvincia) {
        List<YearDataSummary> pivot = birthService.getPivotByYear();
        List<Province> provincias = birthService.getAllProvinces();

        // Encontrar el ID de la provincia por nombre
        String provinciaId = provincias.stream()
                .filter(p -> p.getNameProvince().equalsIgnoreCase(nombreProvincia))
                .map(Province::getIdProvince)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Provincia no encontrada: " + nombreProvincia));

        // Extraer serie temporal para esta provincia
        List<PuntoTemporal> puntos = new ArrayList<>();
        for (YearDataSummary summary : pivot) {
            Integer cantidad = summary.countByColumnBirth().get(provinciaId);
            if (cantidad != null) {
                puntos.add(new PuntoTemporal(summary.year(), cantidad.doubleValue()));
            }
        }

        return puntos;
    }

    /**
     * Obtiene datos históricos para un nivel de instrucción específico.
     */
    private List<PuntoTemporal> obtenerDatosInstruccion(String nombreInstruccion) {
        List<YearDataSummary> pivot = birthService.getPivotYearInstruction();
        List<Instruction> instrucciones = birthService.getAllInstructions();

        // Encontrar el ID de la instrucción por nombre
        String instruccionId = instrucciones.stream()
                .filter(i -> i.getNameInstruction().equalsIgnoreCase(nombreInstruccion))
                .map(Instruction::getIdInstruction)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nivel de instrucción no encontrado: " + nombreInstruccion));

        // Extraer serie temporal para esta instrucción
        List<PuntoTemporal> puntos = new ArrayList<>();
        for (YearDataSummary summary : pivot) {
            Integer cantidad = summary.countByColumnBirth().get(instruccionId);
            if (cantidad != null) {
                puntos.add(new PuntoTemporal(summary.year(), cantidad.doubleValue()));
            }
        }

        return puntos;
    }

    /**
     * Filtra los datos por rango temporal e intervalo.
     */
    private List<PuntoTemporal> filtrarPorRangoTemporal(
            List<PuntoTemporal> datos,
            int anioInicio,
            int anioFin,
            int intervalo) {

        return datos.stream()
                .filter(p -> p.anio() >= anioInicio && p.anio() <= anioFin)
                .filter(p -> (p.anio() - anioInicio) % intervalo == 0)
                .collect(Collectors.toList());
    }

    /**
     * Estima los parámetros A y B del modelo EDO mediante regresión lineal.
     * Utiliza la transformación: ln(N) = ln(A) + B*t
     *
     * @return array [A, B]
     */
    private double[] estimarParametrosEDO(List<PuntoTemporal> datos) {
        int n = datos.size();
        double sumT = 0, sumLnN = 0, sumT2 = 0, sumTLnN = 0;

        for (PuntoTemporal punto : datos) {
            double t = punto.anio();
            double lnN = Math.log(punto.nacimientos());

            sumT += t;
            sumLnN += lnN;
            sumT2 += t * t;
            sumTLnN += t * lnN;
        }

        // Regresión lineal: y = a + bx
        // donde y = ln(N), x = t, a = ln(A), b = B
        double denominador = n * sumT2 - sumT * sumT;
        if (Math.abs(denominador) < 1e-10) {
            throw new RuntimeException("No se puede estimar el modelo: denominador cercano a cero");
        }

        double B = (n * sumTLnN - sumT * sumLnN) / denominador;
        double lnA = (sumLnN - B * sumT) / n;
        double A = Math.exp(lnA);

        return new double[] { A, B };
    }

    /**
     * Genera la serie modelada usando N(t) = A * e^(B*t).
     */
    private List<PuntoTemporal> generarSerieModelada(List<PuntoTemporal> datosObservados, double A, double B) {
        List<PuntoTemporal> modelados = new ArrayList<>();
        for (PuntoTemporal punto : datosObservados) {
            double nModelado = A * Math.exp(B * punto.anio());
            modelados.add(new PuntoTemporal(punto.anio(), nModelado));
        }
        return modelados;
    }

    /**
     * Genera una serie modelada con alta resolución (paso 0.1) para curvas suaves.
     */
    private List<PuntoTemporal> generarSerieModeladaSuave(int anioInicio, int anioFin, double A, double B) {
        List<PuntoTemporal> curve = new ArrayList<>();
        // Generar puntos cada 0.1 años
        for (double t = anioInicio; t <= anioFin; t += 0.1) {
            double nModelado = A * Math.exp(B * t);
            curve.add(new PuntoTemporal(t, nModelado));
        }
        return curve;
    }

    /**
     * Calcula el coeficiente de determinación (R²).
     */
    private double calcularR2(List<PuntoTemporal> observados, List<PuntoTemporal> modelados) {
        // Solo considerar puntos que coincidan en año y sean positivos
        List<Double> obs = new ArrayList<>();
        List<Double> mod = new ArrayList<>();

        for (int i = 0; i < observados.size(); i++) {
            if (observados.get(i).nacimientos() > 0) {
                obs.add(observados.get(i).nacimientos());
                mod.add(modelados.get(i).nacimientos());
            }
        }

        if (obs.isEmpty())
            return 0.0;

        double mediaObs = obs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double ssRes = 0.0, ssTot = 0.0;
        for (int i = 0; i < obs.size(); i++) {
            double residuo = obs.get(i) - mod.get(i);
            ssRes += residuo * residuo;

            double desvMedia = obs.get(i) - mediaObs;
            ssTot += desvMedia * desvMedia;
        }

        if (ssTot == 0)
            return 0.0;
        return 1.0 - (ssRes / ssTot);
    }

    /**
     * Calcula el Error Absoluto Medio (MAE).
     */
    private double calcularMAE(List<PuntoTemporal> observados, List<PuntoTemporal> modelados) {
        double suma = 0.0;
        int count = 0;

        for (int i = 0; i < observados.size(); i++) {
            if (observados.get(i).nacimientos() > 0) {
                suma += Math.abs(observados.get(i).nacimientos() - modelados.get(i).nacimientos());
                count++;
            }
        }

        return count > 0 ? suma / count : 0.0;
    }

    /**
     * Calcula la Raíz del Error Cuadrático Medio (RMSE).
     */
    private double calcularRMSE(List<PuntoTemporal> observados, List<PuntoTemporal> modelados) {
        double suma = 0.0;
        int count = 0;

        for (int i = 0; i < observados.size(); i++) {
            if (observados.get(i).nacimientos() > 0) {
                double residuo = observados.get(i).nacimientos() - modelados.get(i).nacimientos();
                suma += residuo * residuo;
                count++;
            }
        }

        return count > 0 ? Math.sqrt(suma / count) : 0.0;
    }
}
