package com.app.controllers.modeldata;

import com.app.utils.ThemeManagerUtil;
import com.app.utils.LanguageManagerUtil;
import javafx.application.Platform;
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
    @FXML public javafx.scene.layout.StackPane menuButton;
    @FXML public ImageView burgerIcon;
    @FXML public javafx.scene.shape.SVGPath arrowIcon;
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

    // Botones del menubar
    @FXML private Button btnMenuItemAcercaDe;
    @FXML private Button btnMenuItemFrequency;

    // RUTAS DE ESTILOS CSS
    private static final String COLORS_LIGHT_CSS = "/com/app/modeldata/css/colors-light.css";
    private static final String COLORS_DARK_CSS = "/com/app/modeldata/css/colors-dark.css";
    private static final String MAIN_CSS = "/com/app/modeldata/css/main.css";


    // RUTAS DE LAS VISTAS FXML
    private static final String LOGIN_VIEW = "/com/app/modeldata/fxml/login/login-vista.fxml";
    private static final String INICIO_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/inicio.fxml";
    private static final String DATOS_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/datos.fxml";
    private static final String MODELADO_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/modelado.fxml";
    private static final String GRAFICOS_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/graficos.fxml";
    private static final String CONFIGURACION_VIEW_PANEL = "/com/app/modeldata/fxml/panels/sidebar/setting.fxml";
    private static final String ACERCA_DE_VIEW_PANEL = "/com/app/modeldata/fxml/panels/menubar/itemshelp/acerca-de.fxml";
    private static final String FREQUENCY_VIEW_PANEL = "/com/app/modeldata/fxml/panels/menubar/itemsanalizer/frequency-analyzer.fxml";

    // LISTENER PARA CAMBIOS DE IDIOMA EN EL PANEL ACTUAL
    private Runnable currentLocaleListener;


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
            // CARGAR LA VISTA DESDE EL ARCHIVO FXML con ResourceBundle
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            loader.setResources(LanguageManagerUtil.getInstance().getBundle());
            AnchorPane viewPanel = loader.load();
            contentPane.getChildren().setAll(viewPanel);
            AnchorPane.setTopAnchor(viewPanel, 0.0);
            AnchorPane.setBottomAnchor(viewPanel, 0.0);
            AnchorPane.setLeftAnchor(viewPanel, 0.0);
            AnchorPane.setRightAnchor(viewPanel, 0.0);

            // REMOVER LISTENER ANTERIOR
            if (currentLocaleListener != null) {
                LanguageManagerUtil.getInstance().removeLocaleChangeListener(currentLocaleListener);
                currentLocaleListener = null;
            }

            // REGISTRAR NUEVO LISTENER QUE RECARGA ESTE PANEL CON EL NUEVO BUNDLE
            Runnable reload = () -> {
                try {
                    FXMLLoader masLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                    masLoader.setResources(LanguageManagerUtil.getInstance().getBundle());
                    AnchorPane newView = masLoader.load();
                    Platform.runLater(() -> {
                        contentPane.getChildren().setAll(newView);
                        AnchorPane.setTopAnchor(newView, 0.0);
                        AnchorPane.setBottomAnchor(newView, 0.0);
                        AnchorPane.setLeftAnchor(newView, 0.0);
                        AnchorPane.setRightAnchor(newView, 0.0);
                    });
                } catch (Exception e) { }
            };
            currentLocaleListener = reload;
            LanguageManagerUtil.getInstance().addLocaleChangeListener(currentLocaleListener);

            // REGISTRAR EL STAGE PRINCIPAL PARA QUE RECIBA CAMBIOS GLOBALES DE IDIOMA
            try {
                if (contentPane.getScene() != null && contentPane.getScene().getWindow() instanceof Stage stage) {
                    LanguageManagerUtil.getInstance().registerStage(stage, reload);
                }
            } catch (Exception ignored) {}
        } catch (IOException | NullPointerException exception) {
            throw new RuntimeException("Error cargando la vista: " + fxmlPath, exception);
        }
    }

    // MÉTODO PARA CREAR Y MOSTRAR UN NUEVO STAGE CON LA VISTA ESPECIFICADA
    private void createAndShowStage(String fxmlPath, String title) {
        try {
            // CARGAR LA VISTA DESDE EL ARCHIVO FXML con ResourceBundle
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            loader.setResources(LanguageManagerUtil.getInstance().getBundle());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            try {
                ThemeManagerUtil.getInstance().registerStage(stage);
                // REGISTRAR STAGE Y SU ACCIÓN DE RELOAD
                Runnable reload = () -> {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                        fxmlLoader.setResources(LanguageManagerUtil.getInstance().getBundle());
                        Parent newRoot = fxmlLoader.load();
                        Platform.runLater(() -> {
                            Scene sceneHereOCualquierCosa = stage.getScene();
                            if (sceneHereOCualquierCosa != null) sceneHereOCualquierCosa.setRoot(newRoot);
                            ThemeManagerUtil.getInstance().applyToScene(sceneHereOCualquierCosa);
                        });
                    } catch (Exception ignored) {}
                };
                LanguageManagerUtil.getInstance().registerStage(stage, reload);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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

    // MÉTODO PARA TOGGLEAR EL MENÚ (SIDEBAR)
    public void toggleMenu(MouseEvent mouseEvent) {
        boolean currentlyVisible = sideBar.isVisible() && sideBar.isManaged();
        if (currentlyVisible) {
            // ocultar sidebar
            sideBar.setManaged(false);
            sideBar.setVisible(false);
            // mostrar flecha (volver)
            if (burgerIcon != null) burgerIcon.setVisible(false);
            if (arrowIcon != null) arrowIcon.setVisible(true);
        } else {
            // mostrar sidebar
            sideBar.setManaged(true);
            sideBar.setVisible(true);
            // restaurar icono burger
            if (burgerIcon != null) burgerIcon.setVisible(true);
            if (arrowIcon != null) arrowIcon.setVisible(false);
        }
    }

    // MENÚ ÍTEM DE ACERCA DE 
    @FXML
    public void itemOpenAbout(ActionEvent actionEvent) {
        createAndShowStage(ACERCA_DE_VIEW_PANEL, "Acerca de");
    }
    // MENÚ ÍTEM DE FRECUENCIAS
    @FXML
    public void itemOpenFrequency(ActionEvent actionEvent) {
        createAndShowStage(FREQUENCY_VIEW_PANEL,"Frecuencias estadísticas");
    }
    // MÉTODO PARA CERRAR SESIÓN
    @FXML
    public void itemLogout(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(LOGIN_VIEW)));
            loader.setResources(LanguageManagerUtil.getInstance().getBundle());
            Scene loginScene = new Scene(loader.load());

            try {
                ThemeManagerUtil.getInstance().applyToScene(loginScene);
            } catch (Exception e) { throw new RuntimeException(e); }

            Stage mainViewStage = (Stage) mainLayout.getScene().getWindow();
            Stage loginStage = new Stage();
            loginStage.setScene(loginScene);
            loginStage.setTitle("MODELDATA - LOGIN");
            // REGISTRAR LOGINSTAGE PARA ACTUALIZACIONES DE IDIOMA
            try {
                Runnable reload = () -> {
                    try {
                        FXMLLoader otroLoaderMas = new FXMLLoader(Objects.requireNonNull(getClass().getResource(LOGIN_VIEW)));
                        otroLoaderMas.setResources(LanguageManagerUtil.getInstance().getBundle());
                        Scene sceneHereOCualquierCosa = loginStage.getScene();
                        if (sceneHereOCualquierCosa != null) {
                            Parent root = otroLoaderMas.load();
                            Platform.runLater(() -> {
                                sceneHereOCualquierCosa.setRoot(root);
                                ThemeManagerUtil.getInstance().applyToScene(sceneHereOCualquierCosa);
                            });
                        }
                    } catch (Exception ignored) {}
                };
                LanguageManagerUtil.getInstance().registerStage(loginStage, reload);
            } catch (Exception ignored) {}
            loginStage.show();
            mainViewStage.close();
        } catch (RuntimeException | IOException e) { throw new RuntimeException(e); }
    }
}
