package com.app.models.modeling;

/**
 * Enum para los tipos de segmentación disponibles en el modelado.
 */
public enum TipoSegmentacion {
    PROVINCIA("Provincia"),
    INSTRUCCION("Nivel de Instrucción");

    private final String displayName;

    TipoSegmentacion(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
