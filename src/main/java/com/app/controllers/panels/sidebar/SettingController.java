package com.app.controllers.panels.sidebar;

import com.app.utils.LanguageManagerUtil;
import com.app.utils.ThemeManagerUtil;
import com.app.utils.ThemeMode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;

import java.util.Locale;

public class SettingController {

    @FXML public ComboBox cmbLanguages;
    @FXML private RadioButton rdBtnAuto;
    @FXML private RadioButton rdBtnLight;
    @FXML private RadioButton rdBtnDark;
    @FXML private Button btnApply;
    @FXML private Button btnCancel;
    private ThemeMode pendingMode;
    private Locale pendingLocale;
    private Locale currentLocale;

    @FXML
    private void initialize() {
        // INICIALIZAR COMBOBOX DE IDIOMAS
        currentLocale = LanguageManagerUtil.getInstance().getLocale();
        pendingLocale = currentLocale;
        
        ComboBox<Locale> box = (ComboBox<Locale>) cmbLanguages;
        box.setItems(FXCollections.observableArrayList(LanguageManagerUtil.getInstance().getSupportedLocales()));
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(Locale locale) {
                if (locale == null) return "";
                return locale.getDisplayLanguage(LanguageManagerUtil.getInstance().getLocale());
            }

            @Override
            public Locale fromString(String string) { return null; }
        });
        box.getSelectionModel().select(currentLocale);
        box.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) pendingLocale = newValue;
            updateApplyStage();
        });

        ThemeMode noseQuePoner = ThemeManagerUtil.getInstance().getThemeMode();
        pendingMode = noseQuePoner;

        ToggleGroup toggleGroupTheme = new ToggleGroup();
        rdBtnAuto.setToggleGroup(toggleGroupTheme);
        rdBtnLight.setToggleGroup(toggleGroupTheme);
        rdBtnDark.setToggleGroup(toggleGroupTheme);

        applySelectionToRadioButtons(noseQuePoner);
        updateApplyStage();

        // ESCUCHAR CAMBIOS EN LOS RADIO BUTTONS
        toggleGroupTheme.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == rdBtnAuto) pendingMode = ThemeMode.AUTO;
            else if (newToggle == rdBtnLight) pendingMode = ThemeMode.LIGHT;
            else if (newToggle == rdBtnDark) pendingMode = ThemeMode.DARK;
            updateApplyStage();
        } );
    }
    @FXML // MÉTODO PARA APLICAR LOS CAMBIOS DE TEMA E IDIOMA
    public void onBtnApply(ActionEvent actionEvent) {
        ThemeManagerUtil.getInstance().setThemeMode(pendingMode);
        // ASEGURAR QUE LA ESCENA ACTUAL RECIBA LOS ESTILOS
        try { ThemeManagerUtil.getInstance().applyToScene(rdBtnAuto.getScene()); } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // APLICAR CAMBIO DE IDIOMA SI CAMBIÓ
        try {
            if (pendingLocale != null && !pendingLocale.getLanguage().equalsIgnoreCase(LanguageManagerUtil.getInstance().getLocale().getLanguage())) {
                LanguageManagerUtil.getInstance().setLocale(pendingLocale);
            }
        } catch (Exception ignored) {}
        updateApplyStage();
    }
    @FXML // MÉTODO PARA CANCELAR LOS CAMBIOS Y RESTAURAR LA SELECCIÓN ANTERIOR
    public void onBtnCancel(ActionEvent actionEvent) {
        ThemeMode noseQuePoner = ThemeManagerUtil.getInstance().getThemeMode();
        pendingMode = noseQuePoner;
        applySelectionToRadioButtons(noseQuePoner);
        // RESTAURAR SELECCIÓN DE IDIOMA
        pendingLocale = LanguageManagerUtil.getInstance().getLocale();
        @SuppressWarnings("unchecked")
        ComboBox<Locale> box = (ComboBox<Locale>) cmbLanguages;
        box.getSelectionModel().select(pendingLocale);
        updateApplyStage();
    }

    // APLICAR SELECCIÓN DE MODO TEMA A LOS RADIO BUTTONS
    private void applySelectionToRadioButtons(ThemeMode mode) {
        rdBtnAuto.setSelected(mode == ThemeMode.AUTO);
        rdBtnLight.setSelected(mode == ThemeMode.LIGHT);
        rdBtnDark.setSelected(mode == ThemeMode.DARK);
    }

    // ACTUALIZAR EL ESTADO DEL BOTÓN APLICAR
    private void updateApplyStage() {
        boolean changedTheme = pendingMode != ThemeManagerUtil.getInstance().getThemeMode();
        boolean changedLanguage = pendingLocale != null && !pendingLocale.getLanguage().equalsIgnoreCase(LanguageManagerUtil.getInstance().getLocale().getLanguage());
        btnApply.setDisable(!(changedTheme || changedLanguage));
    }
}