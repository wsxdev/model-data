package com.app.controllers.login;

import com.app.models.dao.interfaces.IProvince;
import com.app.models.database.*;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginController {

    @FXML public Button btnIniciarSesion;

    // MÉTODO PARA ABRIR LA VENTANA PRINCIPAL DESPUÉS DE UN LOGIN VÁLIDO
    @FXML
    private void goToMainWindow(ActionEvent event) {
        try {
            // PARA CARGAR LA VENTANA PRINCIPAL E INICIAR DESPUÉS DEL LOGIN VÁLIDO
            FXMLLoader loaderVistaPrincipal = new FXMLLoader(getClass().getResource("/com/app/modeldata/fxml/mainview/vista-principal.fxml"));
            Scene vistaPrincipalScene = new Scene(loaderVistaPrincipal.load());

            vistaPrincipalScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/app/modeldata/css/colors-dark.css")).toExternalForm());
            vistaPrincipalScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/app/modeldata/css/main.css")).toExternalForm());

            // CREAR UN NUEVO STAGE Y CERRAR EL STAGE DE LOGIN
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Stage vistaPrincipalStage = new Stage();
            vistaPrincipalStage.setScene(vistaPrincipalScene);
            vistaPrincipalStage.setTitle("ModelData");

            // CONFIGURAR LA VENTANA PRINCIPAL
            // svistaPrincipalStage.setFullScreen(true);
            vistaPrincipalStage.setMaximized(true);

            // LA LÍNEA DE ABAJO TIENE ALGO QUE FIUEBFUBAUIASBF - ACTIVAR CON PRECAUCIÓN :)
            // vistaPrincipalStage.setResizable(false);

            // AÑADIR ICONO A LA VENTANA SI NO TIENE ICONO ASIGNADO
            try {
                vistaPrincipalStage.getIcons().add(new javafx.scene.image.Image(
                        Objects.requireNonNull(getClass().getResourceAsStream("/com/app/modeldata/images/logos/ModelDataLogoConBG.png"))
                ));
            } catch (Exception ignored) {
                // SI NO SE PUEDE CARGAR EL ICONO, SE IGNORA EL ERROR
            }

            // MOSTRAR LA VENTANA PRINCIPAL Y CERRAR LA DE LOGIN
            vistaPrincipalStage.show();
            loginStage.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
}

}
