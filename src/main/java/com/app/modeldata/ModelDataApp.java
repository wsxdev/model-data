package com.app.modeldata;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;
import com.app.utils.LanguageManagerUtil;
import com.app.utils.ThemeManagerUtil;
import javafx.application.Platform;
import java.util.Objects;

public class ModelDataApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // ASEGURAR TABLAS
        com.app.models.database.DatabaseSetup.initializeTables();

        // PARA CARGAR EL LOGIN ANTES DE LA MAIN VIEW
        FXMLLoader loginLoader = new FXMLLoader(
                ModelDataApp.class.getResource("/com/app/modeldata/fxml/login/login-vista.fxml"));
        // PARA PROVEER EL RESOURCE BUNDLE Y QUE FUNCIONEN LOS %keys EN FXML
        loginLoader.setResources(LanguageManagerUtil.getInstance().getBundle());

        // AÑADIR ICONO AL STAGE
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/com/app/modeldata/images/logos/vistaprincipal/ModelDataLogoConBG.png"))));

        Parent root = loginLoader.load();
        Scene sceneLogin = new Scene(root);
        stage.setScene(sceneLogin);
        stage.setTitle("MODELDATA - LOGIN");
        stage.setResizable(false);
        stage.show();
        // REGISTRAR STAGE PARA SOPORTAR CAMBIOS DE TEMA
        try {
            ThemeManagerUtil.getInstance().registerStage(stage);
            // REGISTRAR STAGE PARA SOPORTAR CAMBIOS DE IDIOMA
            Runnable reload = () -> {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            ModelDataApp.class.getResource("/com/app/modeldata/fxml/login/login-vista.fxml"));
                    loader.setResources(LanguageManagerUtil.getInstance().getBundle());
                    Parent newRoot = loader.load();
                    Platform.runLater(() -> {
                        Scene scene = stage.getScene();
                        if (scene != null)
                            scene.setRoot(newRoot);
                        ThemeManagerUtil.getInstance().applyToScene(scene);
                    });
                } catch (Exception ignored) {
                }
            };
            LanguageManagerUtil.getInstance().registerStage(stage, reload);
        } catch (Exception ignored) {
        }

    }
}