package com.app.controllers.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    // METODO PARA ABRIR LA VENTANA PRINCIPAL DESPUÉS DE UN LOGIN VÁLIDO
    @FXML
    private void goToMainWindow(ActionEvent event) {
        try {
            // PARA CARGAR LA VENTANA PRINCIPAL E INICIAR DESPUÉS DEL LOGIN VÁLIDO
            FXMLLoader loaderVistaPrincipal = new FXMLLoader(getClass().getResource("/com/app/modeldata/fxml/mainview/vista-principal.fxml"));
            Scene vistaPrincipalScene = new Scene(loaderVistaPrincipal.load());

            // CREAR UN NUEVO STAGE Y CERRAR EL STAGE DE LOGIN
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Stage vistaPrincipalStage = new Stage();
            vistaPrincipalStage.setScene(vistaPrincipalScene);
            vistaPrincipalStage.setTitle("ModelData");

            // CONFIGURAR LA VENTANA PRINCIPAL
            vistaPrincipalStage.setFullScreen(false);
            vistaPrincipalStage.setMaximized(true);

            vistaPrincipalStage.setResizable(false);

            // AÑADIR ICONO A LA VENTANA SI NO TIENE ICONO ASIGNADO
            try {
                vistaPrincipalStage.getIcons().add(new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/com/app/modeldata/images/logos/ModelDataLogoConBG.png")
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
