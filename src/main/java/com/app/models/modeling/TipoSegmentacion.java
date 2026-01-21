package com.app.models.modeling;

import com.app.utils.LanguageManagerUtil;
import java.util.ResourceBundle;

/**
 * Enum para los tipos de segmentación disponibles en el modelado.
 */
public enum TipoSegmentacion {
    PROVINCIA("segmentacion.provincia"),
    INSTRUCCION("segmentacion.instruccion");

    private final String key;

    TipoSegmentacion(String key) {
        this.key = key;
    }

    public String getDisplayName() {
        try {
            return LanguageManagerUtil.getInstance().getBundle().getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
