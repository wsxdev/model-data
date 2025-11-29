package com.app.modeldata;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.util.logging.Logger;

public class ModelDataApp extends Application {

    private static Stage mainStage;
    private static final Logger LOGGER = Logger.getLogger(ModelDataApp.class.getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            mainStage = primaryStage;

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/app/modeldata/vistas/LoginView.fxml")
            );

            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("ModelData - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            LOGGER.severe("Error cargando la vista inicial: " + e.getMessage());
        }
    }

    public static void changeScene(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ModelDataApp.class.getResource("/com/app/modeldata/vistas/" + fxml)
            );

            Scene newScene = new Scene(loader.load());
            mainStage.setScene(newScene);
            mainStage.setTitle(title);
            mainStage.setResizable(false);
            mainStage.show();

        } catch (Exception e) {
            LOGGER.severe("Error cambiando de vista (" + fxml + "): " + e.getMessage());
        }
    }
}
