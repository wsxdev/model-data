package com.app.controllers.panels.menubar.itemsanalizer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
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
