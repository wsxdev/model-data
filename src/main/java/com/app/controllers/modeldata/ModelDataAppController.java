package com.app.controllers.modeldata;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;

public class ModelDataAppController {
    @FXML
    private Label welcomeText;

    // VARIABLES DE LOS BOTONES DEL SIDEBAR
    @FXML private ToggleButton btnInicio;
    @FXML private ToggleButton btnDatos;
    @FXML private ToggleButton btnModelado;
    @FXML private ToggleButton btnGraficos;
    @FXML private ToggleButton btnConfiguracion;

    // GRUPO DE BOTONES DEL SIDEBAR
    private ToggleGroup menuButtonGroupSideBar;
    // PANEL DONDE SE CARGAN LAS VISTAS DE CADA PESTAÑA
    @FXML private AnchorPane contentPane;

    // INICIALIZADOR DEL CONTROLADOR
    @FXML
    public void initialize() {
        // INICIALIZAR EL GRUPO DE BOTONES DEL SIDEBAR
        menuButtonGroupSideBar = new ToggleGroup();
        // AÑADIR BOTONES AL GRUPO
        btnInicio.setToggleGroup(menuButtonGroupSideBar);
        btnDatos.setToggleGroup(menuButtonGroupSideBar);
        btnModelado.setToggleGroup(menuButtonGroupSideBar);
        btnGraficos.setToggleGroup(menuButtonGroupSideBar);
        btnConfiguracion.setToggleGroup(menuButtonGroupSideBar);

        // GESTIONAR EL CAMBIO DE VISTA SEGÚN EL BOTÓN SELECCIONADO
        menuButtonGroupSideBar.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) {
                // OBTENER EL BOTÓN SELECCIONADO
                ToggleButton selected = (ToggleButton) newT;

                // CARGAR LA VISTA CORRESPONDIENTE SEGÚN EL BOTÓN SELECCIONADO
                switch (selected.getText()) {
                    case "INICIO" -> loadViewPanels("/com/app/modeldata/fxml/panels/inicio.fxml");
                    case "DATOS" -> loadViewPanels("/com/app/modeldata/fxml/panels/datos.fxml");
                    case "MODELADO" -> loadViewPanels("/com/app/modeldata/fxml/panels/modelado.fxml");
                    case "GRÁFICOS" -> loadViewPanels("/com/app/modeldata/fxml/panels/graficos.fxml");
                    case "CONFIGURACIÓN" -> loadViewPanels("/com/app/modeldata/fxml/panels/configuracion.fxml");
                }
            }
        });

        // CARGAR LA VISTA INICIAL (INICIO)
        loadViewPanels("/com/app/modeldata/fxml/panels/inicio.fxml");
        btnInicio.setSelected(true);

    }

    // MÉTODO PARA CARGAR LAS VISTAS EN EL PANEL DE CONTENIDO
    private void loadViewPanels(String fxmlPath) {
        try {
            // CARGAR LA VISTA DESDE EL ARCHIVO FXML
            AnchorPane viewPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            contentPane.getChildren().setAll(viewPanel);
            AnchorPane.setTopAnchor(viewPanel, 0.0);
            AnchorPane.setBottomAnchor(viewPanel, 0.0);
            AnchorPane.setLeftAnchor(viewPanel, 0.0);
            AnchorPane.setRightAnchor(viewPanel, 0.0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    // MÉTODO PARA TOGGLEAR EL MENÚ (SIDEBAR) - POR IMPLEMENTAR
    public void toggleMenu(MouseEvent mouseEvent) {
    }
}
