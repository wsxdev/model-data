package com.app.models.modeling;

import java.util.List;

/**
 * Record para almacenar los resultados completos del modelado EDO.
 * Incluye parámetros estimados, métricas de ajuste, y series temporales.
 */
public record ResultadoModeladoEDO(
        String categoria, // Ej: "Guayas", "Educación Básica"
        TipoSegmentacion tipo, // PROVINCIA o INSTRUCCION
        double parametroA, // Condición inicial (A en N(t) = A*e^(B*t))
        double parametroB, // Tasa de cambio (B en N(t) = A*e^(B*t))
        double r2, // Coeficiente de determinación
        double mae, // Error absoluto medio
        double rmse, // Raíz del error cuadrático medio
        List<PuntoTemporal> observados, // Datos históricos reales
        List<PuntoTemporal> modelados, // Serie modelada N(t) = A*e^(B*t) (Puntos discretos coincidientes con
                                       // observados)
        List<PuntoTemporal> modeladosCurve // Serie modelada suave para graficación (alta resolución)
) {

    /**
     * Genera la ecuación ajustada en formato legible.
     * 
     * @return String con la ecuación N(t) = A · e^(B·t)
     */
    public String getEcuacionFormateada() {
        return String.format("N(t) = %.4e · e^(%.6f·t)", parametroA, parametroB);
    }

    /**
     * Determina si el modelo indica crecimiento o decrecimiento.
     * 
     * @return "Crecimiento" si B > 0, "Decrecimiento" si B <= 0
     */
    public String getTipoCrecimiento() {
        return parametroB > 0 ? "Crecimiento" : "Decrecimiento";
    }
}
