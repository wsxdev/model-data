package com.app.utils;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

public final class DialogUtil {
    // PREVIA INSTANCIA
    public DialogUtil() {}
    // ICONO DE LA APLICACIÓN
    private static final Image iconApp = new Image(Objects.requireNonNull(DialogUtil.class.getResourceAsStream("/com/app/modeldata/images/logos/vistaprincipal/ModelDataLogoConBG.png")));

    // MÉTODO PARA MOSTRAR DIÁLOGO DE ADVERTENCIA
    public static void showWarningDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image iconAlert = new Image(Objects.requireNonNull(DialogUtil.class.getResourceAsStream("/com/app/modeldata/images/icons/warning-icon.png")));
        ImageView imageView = new ImageView(iconAlert);
        alert.getDialogPane().setGraphic(imageView); // Para la información
        stage.getIcons().add(iconApp);
        alert.showAndWait();
    }

    // MÉTODO PARA MOSTRAR DIÁLOGO DE ERROR
    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image iconAlert = new Image(Objects.requireNonNull(DialogUtil.class.getResourceAsStream("/com/app/modeldata/images/icons/error-icon.png")));
        ImageView imageView = new ImageView(iconAlert);
        alert.getDialogPane().setGraphic(imageView); // Para la información
        stage.getIcons().add(iconApp);
        alert.showAndWait();
    }

    // MÉTODO PARA MOSTRAR DIÁLOGO DE CONFIRMACIÓN
    public static void showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // MÉTODO PARA MOSTRAR DIÁLOGO DE INFORMACIÓN
    public static void showInformationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image iconAlert = new Image(Objects.requireNonNull(DialogUtil.class.getResourceAsStream("/com/app/modeldata/images/icons/info-icon.png")));
        ImageView imageView = new ImageView(iconAlert);
        alert.getDialogPane().setGraphic(imageView); // Para la información
        stage.getIcons().add(iconApp);
        alert.showAndWait();
    }

    // MÉTODO PARA MOSTRAR DIÁLOGO DE CARGA
    public static Stage showLoadingDialog(String title, String message) {
        Stage stageLoading = new Stage();
        stageLoading.initModality(Modality.APPLICATION_MODAL);
        stageLoading.setResizable(false);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label label = new Label(message == null ? "Cargando..." : message);
        VBox box = new VBox(10, progressIndicator,label);
        box.setAlignment(Pos.CENTER);
        Scene scene = new Scene(box);

        stageLoading.setScene(scene);
        stageLoading.setTitle("ModelData");
        stageLoading.getIcons().add(iconApp);
        stageLoading.setWidth(400);
        stageLoading.setHeight(150);
        stageLoading.show();

        return stageLoading;
    }
}
