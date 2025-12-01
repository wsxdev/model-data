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

            // REUTILIZAR EL MISMO STAGE DEL LOGIN PARA LA VENTANA PRINCIPAL
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // SE ESTABLECE LA NUEVA SCENE DE LA VENTANA PRINCIPAL
            stage.setScene(vistaPrincipalScene);
            stage.setTitle("ModelData");

            // PARA MAXIMIZAR LA VENTANA PRINCIPAL AL INICIAR
            stage.setMaximized(true);
            stage.setResizable(false);

            // AÑADIR ICONO A LA VENTANA SI NO TIENE ICONO ASIGNADO
            if (stage.getIcons().isEmpty()) {
                try {
                    stage.getIcons().add(new javafx.scene.image.Image(
                            getClass().getResourceAsStream("/com/app/modeldata/images/logos/ModelDataLogoConBG.png")
                    ));
                } catch (Exception ignored) {
                    // SI HAY UN ERROR AL CARGAR EL ICONO, SIMPLEMENTE NO SE AÑADE
                }
            }
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
}

}
