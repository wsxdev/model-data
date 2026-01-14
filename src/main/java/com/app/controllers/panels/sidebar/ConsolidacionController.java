package com.app.controllers.panels.sidebar;

import com.app.models.services.BirthService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConsolidacionController {

    @FXML
    private ComboBox<Integer> cbYear;
    @FXML
    private Button btnConsolidate;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressIndicator progressIndicator;

    private final BirthService birthService = new BirthService();

    @FXML
    public void initialize() {
        loadYears();
        progressIndicator.setVisible(false);
    }

    private void loadYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int i = 2000; i <= currentYear; i++) {
            years.add(i);
        }
        cbYear.setItems(FXCollections.observableArrayList(years));
    }

    @FXML
    public void onConsolidate() {
        Integer year = cbYear.getValue();
        if (year == null) {
            statusLabel.setText("Seleccione un año.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        statusLabel.setText("Consolidando datos para el año " + year + "...");
        statusLabel.setStyle("-fx-text-fill: black;");
        btnConsolidate.setDisable(true);
        progressIndicator.setVisible(true);

        // Run in background to not freeze UI
        CompletableFuture.runAsync(() -> {
            try {
                // Consolidate Provinces
                birthService.consolidateProvinces(year);
                // Consolidate Instructions
                birthService.consolidateInstructions(year);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).handle((result, ex) -> {
            javafx.application.Platform.runLater(() -> {
                btnConsolidate.setDisable(false);
                progressIndicator.setVisible(false);
                if (ex != null) {
                    statusLabel.setText("Error en consolidación: " + ex.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                    ex.printStackTrace();
                } else {
                    statusLabel.setText("Consolidación completada exitosamente.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                }
            });
            return null;
        });
    }
}
