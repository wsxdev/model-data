package com.app.controllers.panels.sidebar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class DatosController implements Initializable {


    @FXML private ComboBox datosAnalizarComboBox;

    @Override
    public void initialize (URL url,ResourceBundle resourceBundle) {
        ObservableList<String> datosComboBox = FXCollections.observableArrayList("Provincia - año", "Instrucción - año");
        datosAnalizarComboBox.setItems(datosComboBox);

    }
    public void seleccionarDatosAnalizar(ActionEvent actionEvent) {
    }
}