package com.app.controllers.login;

import com.app.utils.ThemeManagerUtil;
import com.app.utils.LanguageManagerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.Parent;
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
            // PARA PROVEER EL RESOURCE BUNDLE Y QUE FUNCIONEN LOS %keys EN FXML
            loaderVistaPrincipal.setResources(LanguageManagerUtil.getInstance().getBundle());
            Scene vistaPrincipalScene = new Scene(loaderVistaPrincipal.load());

            Stage vistaPrincipalStage = new Stage();
            vistaPrincipalStage.setScene(vistaPrincipalScene);
            try {
                ThemeManagerUtil.getInstance().registerStage(vistaPrincipalStage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // CREAR UN NUEVO STAGE Y CERRAR EL STAGE DE LOGIN
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            vistaPrincipalStage.setTitle("ModelData");

            // CONFIGURAR LA VENTANA PRINCIPAL
            // svistaPrincipalStage.setFullScreen(true);
            vistaPrincipalStage.setMaximized(true);

            // LA LÍNEA DE ABAJO TIENE ALGO QUE FIUEBFUBAUIASBF - ACTIVAR CON PRECAUCIÓN :)
            // vistaPrincipalStage.setResizable(false);

            // AÑADIR ICONO A LA VENTANA SI NO TIENE ICONO ASIGNADO
            try {
                vistaPrincipalStage.getIcons().add(new javafx.scene.image.Image(
                        Objects.requireNonNull(getClass().getResourceAsStream("/com/app/modeldata/images/logos/vistaprincipal/ModelDataLogoConBG.png"))
                ));
            } catch (Exception ignored) {
                // SI NO SE PUEDE CARGAR EL ICONO, SE IGNORA EL ERROR
            }

            // MOSTRAR LA VENTANA PRINCIPAL Y CERRAR LA DE LOGIN
            vistaPrincipalStage.show();
            // REGISTRAR STAGE PARA ACTUALIZACIONES DE IDIOMA
            try {
                Runnable reload = () -> {
                    try {
                        FXMLLoader loaderHere = new FXMLLoader(getClass().getResource("/com/app/modeldata/fxml/mainview/vista-principal.fxml"));
                        loaderHere.setResources(LanguageManagerUtil.getInstance().getBundle());
                        Scene sceneHere = vistaPrincipalStage.getScene();
                        if (sceneHere != null) {
                            Parent root = loaderHere.load();
                            Platform.runLater(() -> {
                                sceneHere.setRoot(root);
                                try { ThemeManagerUtil.getInstance().applyToScene(sceneHere); } catch (Exception ignored) {}
                            });
                        }
                    } catch (Exception ignored) {}
                };
                LanguageManagerUtil.getInstance().registerStage(vistaPrincipalStage, reload);
            } catch (Exception ignored) {}
            loginStage.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
}

}
