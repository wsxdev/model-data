package com.app.controllers.login;

import com.app.models.services.AuthService;
import com.app.models.services.UserSession;
import com.app.utils.DialogUtil;
import com.app.utils.LanguageManagerUtil;
import com.app.utils.ThemeManagerUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    public Button btnIniciarSesion;

    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    private void goToMainWindow(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            DialogUtil.showErrorDialog("Error", "Por favor ingrese usuario y contraseña.");
            return;
        }

        if (authService.login(username, password)) {
            loadMainWindow(event);
        } else {
            DialogUtil.showErrorDialog("Error", "Credenciales incorrectas.");
        }
    }

    private void loadMainWindow(ActionEvent event) {
        try {
            ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/app/modeldata/fxml/mainview/vista-principal.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("ModelData - " + UserSession.getInstance().getCurrentUser().getUsername());
            stage.setMaximized(true);

            // Setup Theme
            try {
                ThemeManagerUtil.getInstance().registerStage(stage);
            } catch (Exception ignored) {
            }

            // Setup Icon
            try {
                stage.getIcons().add(new javafx.scene.image.Image(
                        Objects.requireNonNull(getClass().getResourceAsStream(
                                "/com/app/modeldata/images/logos/vistaprincipal/ModelDataLogoConBG.png"))));
            } catch (Exception ignored) {
            }

            // Close Login Stage
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loginStage.close();

            stage.show();

            // Register for language updates
            registerLanguageUpdate(stage);

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtil.showErrorDialog("Error Crítico", "No se pudo cargar la ventana principal: " + e.getMessage());
        }
    }

    private void registerLanguageUpdate(Stage stage) {
        try {
            Runnable reload = () -> {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/com/app/modeldata/fxml/mainview/vista-principal.fxml"));
                    loader.setResources(LanguageManagerUtil.getInstance().getBundle());
                    Scene scene = stage.getScene();
                    if (scene != null) {
                        Parent root = loader.load();
                        Platform.runLater(() -> {
                            scene.setRoot(root);
                            try {
                                ThemeManagerUtil.getInstance().applyToScene(scene);
                            } catch (Exception ignored) {
                            }
                        });
                    }
                } catch (Exception ignored) {
                }
            };
            LanguageManagerUtil.getInstance().registerStage(stage, reload);
        } catch (Exception ignored) {
        }
    }
}
