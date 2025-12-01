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

            // SE CIERA LA VENTANA ACTUAL (LOGIN)
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loginStage.close();

            // ABRIR VENTANA PRINCIPAL
            Stage newStageVistaPrincipal = new Stage();
            newStageVistaPrincipal.setScene(vistaPrincipalScene);
            newStageVistaPrincipal.setTitle("ModelData");
            // MOSTRAR LA VENTANA MAXIMIZADA
            newStageVistaPrincipal.setMaximized(true);
            // PARA QUE NO SE PUEDA REDIMENSIONAR LA VENTANA
            newStageVistaPrincipal.setResizable(false);
            newStageVistaPrincipal.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
}

}
