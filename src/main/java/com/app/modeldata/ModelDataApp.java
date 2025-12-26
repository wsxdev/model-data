package com.app.modeldata;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ModelDataApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // PARA CARGAR EL LOGIN ANTES DE LA MAIN VIEW
        FXMLLoader loginLoader = new FXMLLoader(ModelDataApp.class.getResource("/com/app/modeldata/fxml/login/login-vista.fxml"));

        // AÑADIR ICONO AL STAGE
    stage.getIcons().add(new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/com/app/modeldata/images/logos/ModelDataLogoConBG.png"))));

        Parent root = loginLoader.load();
        Scene sceneLogin = new Scene(root);
        stage.setScene(sceneLogin);
        stage.setTitle("MODELDATA - LOGIN");
        stage.setResizable(false);
        stage.show();

    }
}