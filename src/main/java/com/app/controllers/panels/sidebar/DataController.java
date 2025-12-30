package com.app.controllers.panels.sidebar;

import com.app.models.entities.Instruction;
import com.app.models.entities.Province;
import com.app.models.services.BirthService;
import com.app.models.services.YearDataSummary;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DataController implements Initializable {

    @FXML private TableView<YearDataSummary> tableData;
    @FXML private ComboBox<String> datosAnalizarComboBox;

    @Override
    public void initialize (URL url,ResourceBundle resourceBundle) {
        if (datosAnalizarComboBox.getItems() == null || datosAnalizarComboBox.getItems().isEmpty()) {
            ObservableList<String> datosComboBox = FXCollections.observableArrayList("Provincia - año", "Instrucción - año");
            datosAnalizarComboBox.setItems(datosComboBox);
        }
    }
    @FXML
    public void btnDatosAnalizar(ActionEvent actionEvent) {

        String selected = datosAnalizarComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        boolean birthsProvincesSelected = "Provincia - año".equals(selected);
        boolean birthsInstructionsSelected = "Instrucción - año".equals(selected);

        // OBTIENE LOS VALORES ORDENADOS QUE RETORNA LOS MÉTODOS DE BIRTH SERVICE
        BirthService birthService = new BirthService();

        tableData.getColumns().clear();
        TableColumn<YearDataSummary, Integer> columnYear = new TableColumn<>("Año");
        columnYear.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().year()));
        tableData.getColumns().add(columnYear);

        if (birthsProvincesSelected){
            List<YearDataSummary> rows = birthService.getPivotByYear();
            List<Province> provinces = birthService.getProvinceOrderBirths();

            for (Province prov :  provinces) {
                String provId = prov.getIdProvince();
                TableColumn<YearDataSummary, Integer> columnProvince = new TableColumn<>(prov.getNameProvince());
                columnProvince.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCountForRecord(provId)));
                tableData.getColumns().add(columnProvince);
            }
            ObservableList<YearDataSummary> items = FXCollections.observableArrayList(rows);
            tableData.setItems(items);
            return;
        }

        if (birthsInstructionsSelected){
            List<YearDataSummary> rowsI = birthService.getPivotYearInstruction();
            List<Instruction> instructions = birthService.getInstructionOrderBirths();

            for (Instruction instr : instructions) {
                String instructionId = instr.getIdInstruction();
                TableColumn<YearDataSummary, Integer> columnInstruction = new TableColumn<>(instr.getNameInstruction());
                columnInstruction.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCountForRecord(instructionId)));
                tableData.getColumns().add(columnInstruction);
            }
            ObservableList<YearDataSummary> items = FXCollections.observableArrayList(rowsI);
            tableData.setItems(items);
            return;
        }
    }

    public void OpenTableData(SortEvent<TableView> tableViewSortEvent) {

    }
}