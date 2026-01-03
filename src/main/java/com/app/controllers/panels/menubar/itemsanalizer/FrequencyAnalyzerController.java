package com.app.controllers.panels.menubar.itemsanalizer;

import javafx.event.ActionEvent;
import com.app.utils.LanguageManagerUtil;
import com.app.utils.ThemeManagerUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class FrequencyAnalyzerController {

    @FXML private Button btnEstadisticos;
    @FXML private Button btnGraficos;
    @FXML private Button btnAceptarFrecuencias;
    @FXML private Button btnRestablecer;
    @FXML private Button btnCancelarFrecuencias;

    private static final String STATISTICAL_ANALYZER_VIEW = "/com/app/modeldata/fxml/panels/menubar/itemsanalizer/statistical-analyzer.fxml";
    private static final String ANALYZER_GRAPHICS_VIEW = "/com/app/modeldata/fxml/panels/menubar/itemsanalizer/analyzer-graphics.fxml";


    private void createAndShowStage(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            loader.setResources(LanguageManagerUtil.getInstance().getBundle());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            if (btnEstadisticos != null && btnEstadisticos.getScene() != null) {
                stage.initOwner(btnEstadisticos.getScene().getWindow());
            } else if (btnGraficos != null && btnGraficos.getScene() != null) {
                stage.initOwner(btnGraficos.getScene().getWindow());
            }
            // REGISTRAR STAGE PARA SOPORTAR CAMBIOS DE TEMA E IDIOMA
            try {
                ThemeManagerUtil.getInstance().registerStage(stage);
                Runnable reload = () -> {
                    try {
                        // RELOAD DEL FXML CON EL NUEVO BUNDLE
                        FXMLLoader loaderHere = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                        loaderHere.setResources(LanguageManagerUtil.getInstance().getBundle());
                        Parent newRoot = loaderHere.load();
                        Platform.runLater(() -> {
                            Scene sceneHere = stage.getScene();
                            if (sceneHere != null) sceneHere.setRoot(newRoot);
                            ThemeManagerUtil.getInstance().applyToScene(sceneHere);
                        });
                    } catch (Exception ignored) {}
                };
                LanguageManagerUtil.getInstance().registerStage(stage, reload);
            } catch (Exception ignored) {}

            stage.showAndWait();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public void buttonOpenEstadisticos(ActionEvent actionEvent) {
        createAndShowStage(STATISTICAL_ANALYZER_VIEW, "Estadísticos");
    }

    public void buttonOpenGraficos(ActionEvent actionEvent) {
        createAndShowStage(ANALYZER_GRAPHICS_VIEW, "Gráficos analizados");
    }

    public void buttonOpenAceptarFrecuencias(ActionEvent actionEvent) {
    }

    public void buttonOpenRestablecer(ActionEvent actionEvent) {
    }

    public void buttonOpenCancelarFrecuencias(ActionEvent actionEvent) {
    }
}
