package com.app.controllers.modeldata;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ToggleGroup;

public class ModelDataAppController {
    @FXML
    private Label welcomeText;

    @FXML private ToggleButton btnInicio;
    @FXML private ToggleButton btnDatos;
    @FXML private ToggleButton btnModelado;
    @FXML private ToggleButton btnGraficos;
    @FXML private ToggleButton btnConfig;

    private ToggleGroup menuGroup;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void toggleMenu(MouseEvent mouseEvent) {
    }

    @FXML
    public void initialize() {

        menuGroup = new ToggleGroup();

        btnInicio.setToggleGroup(menuGroup);
        btnDatos.setToggleGroup(menuGroup);
        btnModelado.setToggleGroup(menuGroup);
        btnGraficos.setToggleGroup(menuGroup);
        btnConfig.setToggleGroup(menuGroup);

    }
}