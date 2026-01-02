package com.app.controllers.panels.sidebar;

import com.app.utils.ThemeManagerUtil;
import com.app.utils.ThemeMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class SettingController {

    @FXML public ComboBox cmbLanguages;
    @FXML private RadioButton rdBtnAuto;
    @FXML private RadioButton rdBtnLight;
    @FXML private RadioButton rdBtnDark;
    @FXML private Button btnApply;
    @FXML private Button btnCancel;
    private ThemeMode pendingMode;

    @FXML
    private void initialize() {
        ThemeMode noseQuePoner = ThemeManagerUtil.getInstance().getThemeMode();
        pendingMode = noseQuePoner;

        ToggleGroup toggleGroupTheme = new ToggleGroup();
        rdBtnAuto.setToggleGroup(toggleGroupTheme);
        rdBtnLight.setToggleGroup(toggleGroupTheme);
        rdBtnDark.setToggleGroup(toggleGroupTheme);

        applySelectionToRadioButtons(noseQuePoner);
        updateApplyStage();

        toggleGroupTheme.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == rdBtnAuto) pendingMode = ThemeMode.AUTO;
            else if (newToggle == rdBtnLight) pendingMode = ThemeMode.LIGHT;
            else if (newToggle == rdBtnDark) pendingMode = ThemeMode.DARK;
            updateApplyStage();
        } );
    }
    @FXML
    public void onBtnApply(ActionEvent actionEvent) {
        ThemeManagerUtil.getInstance().setThemeMode(pendingMode);
        // ASEGURAR QUE LA ESCENA ACTUAL RECIBA LOS ESTILOS
        try { ThemeManagerUtil.getInstance().applyToScene(rdBtnAuto.getScene()); } catch (Exception e) {
            throw new RuntimeException(e);
        }
        updateApplyStage();
    }
    @FXML
    public void onBtnCancel(ActionEvent actionEvent) {
        ThemeMode noseQuePoner = ThemeManagerUtil.getInstance().getThemeMode();
        pendingMode = noseQuePoner;
        applySelectionToRadioButtons(noseQuePoner);
        updateApplyStage();
    }

    private void applySelectionToRadioButtons(ThemeMode mode) {
        rdBtnAuto.setSelected(mode == ThemeMode.AUTO);
        rdBtnLight.setSelected(mode == ThemeMode.LIGHT);
        rdBtnDark.setSelected(mode == ThemeMode.DARK);
    }

    private void updateApplyStage() {
        boolean changed = pendingMode != ThemeManagerUtil.getInstance().getThemeMode();
        btnApply.setDisable(!changed);
    }
}