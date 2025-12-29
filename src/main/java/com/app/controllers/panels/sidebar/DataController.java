package com.app.controllers.panels.sidebar;

import com.app.models.dao.implementations.BirthInstructionImpl;
import com.app.models.dao.implementations.BirthProvinceImpl;
import com.app.models.dao.implementations.InstructionImpl;
import com.app.models.dao.implementations.ProvinceImpl;
import com.app.models.dao.interfaces.IBirthInstruction;
import com.app.models.dao.interfaces.IBirthProvince;
import com.app.models.dao.interfaces.IInstruction;
import com.app.models.dao.interfaces.IProvince;
import com.app.models.entities.Province;
import com.app.models.services.BirthService;
import com.app.models.services.YearProvinceSummary;
import javafx.beans.property.ReadOnlyListWrapper;
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


    @FXML private TableView<YearProvinceSummary> tableData;
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
        IProvince province = new ProvinceImpl();
        // province.getProvinces();

        IInstruction instruction = new InstructionImpl();
        // instruction.getInstruction();
        IBirthProvince birthProvince = new BirthProvinceImpl();
        // birthProvince.getBirthProvinces();
        IBirthInstruction birthInstructions = new BirthInstructionImpl();
        // birthInstructions.getBirthInstruction();
        BirthService oa = new BirthService();
        // System.out.println(oa.getPivotByYear());

        String selected = datosAnalizarComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // OBTIENE LOS VALORES ORDENADOS QUE RETORNA LOS MÉTODOS DE BIRTH SERVICE
        BirthService birthService = new BirthService();
        List<YearProvinceSummary> rows = birthService.getPivotByYear();
        List<Province> provinces = birthService.getProvinceOrderBirths();

        tableData.getColumns().clear();
        TableColumn<YearProvinceSummary, Integer> columnYear = new TableColumn<>("Año");
        columnYear.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getYear()));
        tableData.getColumns().add(columnYear);

        for (Province prov :  provinces) {
            String provId = prov.getIdProvince();
            TableColumn<YearProvinceSummary, Integer> columnProvince = new TableColumn<>(prov.getNameProvince());
            columnProvince.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCountForProvince(provId)));
            tableData.getColumns().add(columnProvince);
        }

        ObservableList<YearProvinceSummary> items = FXCollections.observableArrayList(rows);
        tableData.setItems(items);


    }


    public void OpenTableData(SortEvent<TableView> tableViewSortEvent) {

    }
}