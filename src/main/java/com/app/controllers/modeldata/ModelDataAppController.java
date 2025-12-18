package com.app.controllers.modeldata;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ModelDataAppController {

    // COMPONENTES FXML DE LA INTERFAZ
    // Layout principal
    @FXML public VBox sideBar;
    @FXML public BorderPane mainLayout;
    @FXML public HBox topBar;
    @FXML public ImageView menuButton;
    @FXML public TextField searchField;
    @FXML public MenuBar menuBar;
    @FXML private Label welcomeText;

    // Panel de contenido donde se cargarán las vistas
    @FXML private AnchorPane contentPane;

    // Botones del sidebar
    @FXML private ToggleButton btnInicio;
    @FXML private ToggleButton btnDatos;
    @FXML private ToggleButton btnModelado;
    @FXML private ToggleButton btnGraficos;
    @FXML private ToggleButton btnConfiguracion;

    // Botones del manubar
    @FXML private Button btnMenuItemAcercaDe;
    @FXML private Button btnMenuItemFrequency;

    // RUTAS DE LAS VISTAS FXML
    private static final String INICIO_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/inicio.fxml";
    private static final String DATOS_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/datos.fxml";
    private static final String MODELADO_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/modelado.fxml";
    private static final String GRAFICOS_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/graficos.fxml";
    private static final String CONFIGURACION_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/configuracion.fxml";
    private static final String ACERCA_DE_VIEW_PANEL = "/com/app/modeldata/fxml/panels/menubar/itemshelp/acerca-de.fxml";
    private static final String FREQUENCY_VIEW_PANEL = "/com/app/modeldata/fxml/panels/menubar/itemsanalizer/frequency-analyzer.fxml";


    // MAPA DE VISTAS ASOCIADAS A LOS BOTONES DEL SIDEBAR
    private static final Map<String, String> SIDEBAR_VIEWS = Map.of(
            "INICIO", INICIO_VIEW_PANEL,
            "DATOS", DATOS_VIEW_PANEL,
            "MODELADO", MODELADO_VIEW_PANEL,
            "GRÁFICOS", GRAFICOS_VIEW_PANEL,
            "CONFIGURACIÓN", CONFIGURACION_VIEW_PANEL
    );

    // INICIALIZADOR DEL CONTROLADOR
    @FXML
    public void initialize() {
        // INICIALIZAR EL GRUPO DE TOGGLE DEL SIDEBAR
        initSideBarToggleGroup();
        // CARGAR LA VISTA INICIAL
        loadInitialView();
    }

    // MÉTODO PARA INICIALIZAR EL GRUPO DE TOGGLE DEL SIDEBAR
    private void initSideBarToggleGroup() {
        ToggleGroup sidebarGroup = new ToggleGroup();

        btnInicio.setToggleGroup(sidebarGroup);
        btnDatos.setToggleGroup(sidebarGroup);
        btnModelado.setToggleGroup(sidebarGroup);
        btnGraficos.setToggleGroup(sidebarGroup);
        btnConfiguracion.setToggleGroup(sidebarGroup);

        sidebarGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle instanceof ToggleButton selectedButton) {
                String viewPath = SIDEBAR_VIEWS.get(selectedButton.getText());
                if (viewPath != null) {
                    loadViewPanels(viewPath);
                }
            }
        });
    }

    // MÉTODO PARA CARGAR LA VISTA INICIAL
    private void loadInitialView() {
        loadViewPanels(INICIO_VIEW_PANEL);
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
        } catch (IOException | NullPointerException exception) {
            throw new RuntimeException("Error cargando la vista: " + fxmlPath, exception);
        }
    }

    private void createAndShowStage(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // EVENTOS FXML DE LA INTERFAZ
    @FXML // NO TOCAR >:(
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    // MÉTODO PARA TOGGLEAR EL MENÚ (SIDEBAR) - POR IMPLEMENTAR
    public void toggleMenu(MouseEvent mouseEvent) {
    }

    @FXML
    public void itemOpenAbout(ActionEvent actionEvent) {
        createAndShowStage(ACERCA_DE_VIEW_PANEL, "Acerca de");
    }
    @FXML
    public void itemOpenFrequency(ActionEvent actionEvent) {
        createAndShowStage(FREQUENCY_VIEW_PANEL,"Frecuencias estadísticos");
    }

}
