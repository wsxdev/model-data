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
import com.app.utils.LanguageManagerUtil;
import java.util.ResourceBundle;
import java.text.MessageFormat;

public class ConsolidacionController {

    @FXML
    private ComboBox<Integer> cbYear;
    @FXML
    private Button btnConsolidate;
    @FXML
    private Button btnDeleteConsolidation;
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
        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();

        if (year == null) {
            statusLabel.setText(bundle.getString("consolidacion.status.selectYear"));
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Warning for past years
        if (year <= 2024) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle(bundle.getString("consolidacion.alert.title"));
            alert.setHeaderText(MessageFormat.format(bundle.getString("consolidacion.alert.header"), year));
            alert.setContentText(bundle.getString("consolidacion.alert.content"));

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (!result.isPresent() || result.get() != javafx.scene.control.ButtonType.OK) {
                return;
            }
        }

        statusLabel.setText(MessageFormat.format(bundle.getString("consolidacion.status.running"), year));
        statusLabel.setStyle("-fx-text-fill: -color-text-primary;");
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
                    statusLabel.setText(
                            MessageFormat.format(bundle.getString("consolidacion.status.error"), ex.getMessage()));
                    statusLabel.setStyle("-fx-text-fill: red;");
                    ex.printStackTrace();
                } else {
                    statusLabel.setText(bundle.getString("consolidacion.status.success"));
                    statusLabel.setStyle("-fx-text-fill: green;");
                }
            });
            return null;
        });
    }

    @FXML
    public void onDeleteConsolidation() {
        Integer year = cbYear.getValue();
        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();

        if (year == null) {
            statusLabel.setText(bundle.getString("consolidacion.status.selectYear"));
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("consolidacion.delete.alert.title"));
        alert.setHeaderText(bundle.getString("consolidacion.delete.alert.header"));
        alert.setContentText(MessageFormat.format(bundle.getString("consolidacion.delete.alert.content"), year));

        java.util.Optional<javafx.scene.control.ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == javafx.scene.control.ButtonType.OK) {
            statusLabel.setText(MessageFormat.format(bundle.getString("consolidacion.delete.status.running"), year));
            statusLabel.setStyle("-fx-text-fill: -color-text-primary;");
            btnDeleteConsolidation.setDisable(true);
            progressIndicator.setVisible(true);

            CompletableFuture.runAsync(() -> {
                birthService.deleteConsolidation(year);
            }).handle((result, ex) -> {
                javafx.application.Platform.runLater(() -> {
                    btnDeleteConsolidation.setDisable(false);
                    progressIndicator.setVisible(false);
                    if (ex != null) {
                        statusLabel.setText(
                                MessageFormat.format(bundle.getString("consolidacion.status.error"), ex.getMessage()));
                        statusLabel.setStyle("-fx-text-fill: red;");
                    } else {
                        statusLabel.setText(
                                MessageFormat.format(bundle.getString("consolidacion.delete.status.success"), year));
                        statusLabel.setStyle("-fx-text-fill: blue;");
                    }
                });
                return null;
            });
        }
    }
}
