package com.app.controllers.panels.sidebar;

import com.app.models.dao.implementations.BirthInstructionImpl;
import com.app.models.dao.implementations.InstructionImpl;
import com.app.models.dao.implementations.ProvinceImpl;
import com.app.models.dao.interfaces.IInstruction;
import com.app.models.dao.interfaces.IProvince;
import com.app.models.entities.Instruction;
import com.app.models.entities.Province;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DataController implements Initializable {


    @FXML private ComboBox<String> datosAnalizarComboBox;

    @Override
    public void initialize (URL url,ResourceBundle resourceBundle) {
        ObservableList<String> datosComboBox = FXCollections.observableArrayList("Provincia - año", "Instrucción - año");
        datosAnalizarComboBox.setItems(datosComboBox);

    }
    @FXML
    public void btnDatosAnalizar(ActionEvent actionEvent) {
        IProvince province = new ProvinceImpl();
        province.getProvinces();

        IInstruction instruction = new InstructionImpl();
        instruction.getInstruction();
    }


    public void OpenTableData(SortEvent<TableView> tableViewSortEvent) {
    }
}