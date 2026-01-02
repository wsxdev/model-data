package com.app.controllers.panels.sidebar;

import com.app.models.entities.Instruction;
import com.app.models.entities.Province;
import com.app.models.services.BirthService;
import com.app.models.services.records.DataResult;
import com.app.models.services.records.ColumnHeader;
import com.app.models.services.DataCache;
import com.app.models.services.records.YearDataSummary;
import com.app.utils.DialogUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
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

        // RESTAURAR TABLA SI HAY DATOS EN CACHÉ
        DataCache cache = DataCache.getInstance();
        if (cache.hasCache()) {
            buildTable(cache.getLastRows(), cache.getLastHeaders());
        }
    }
    @FXML
    public void btnDatosAnalizar(ActionEvent actionEvent) {

        String selected = datosAnalizarComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogUtil.showInformationDialog("ModelData", "No ha seleccionado ninguna opción. Por favor, seleccione una opción para cargar. :)");
            return;
        }
        loadData(selected, true);
    }

    private void buildTable(List<YearDataSummary> rows, List<ColumnHeader> headers) {
        tableData.getColumns().clear();
        TableColumn<YearDataSummary, Integer> columnYear = new TableColumn<>("Año nuevo"); // Happy new year! :)
        columnYear.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().year()));
        tableData.getColumns().add(columnYear);

        for (ColumnHeader header : headers) {
            String id = header.id();
            TableColumn<YearDataSummary, Integer> columnColumnYear = new TableColumn<>(header.name());
            columnColumnYear.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCountForRecord(id)));
            tableData.getColumns().add(columnColumnYear);
        }
        ObservableList<YearDataSummary> items = FXCollections.observableArrayList(rows);
        tableData.setItems(items);
    }

    private void loadData(String selected, boolean forceUpdate) {
        DataCache cache = DataCache.getInstance();
        //
        if (!forceUpdate && cache.hasCache() && selected.equals(cache.getLastSelection())) {
            buildTable(cache.getLastRows(), cache.getLastHeaders());
            return;
        }

        Stage loadingStage = DialogUtil.showLoadingDialog("Cargando", "Cargando datos... Cálmese, no sea impaciente. >:(");
        Task<DataResult> task = new Task<>() {
            @Override
            protected DataResult call() throws Exception {
                BirthService birthService = new BirthService();
                if ("Provincia - año".equals(selected)) {
                    List<YearDataSummary> rows = birthService.getPivotByYear();
                    List<Province> provinces = birthService.getProvinceOrderBirths();
                    List<ColumnHeader> headers = new ArrayList<>();

                    for (Province prov : provinces) headers.add(new ColumnHeader(prov.getIdProvince(), prov.getNameProvince()));
                    return new DataResult(rows, headers);
                }
                if ("Instrucción - año".equals(selected)) {
                    List<YearDataSummary> rows = birthService.getPivotYearInstruction();
                    List<Instruction> instructions = birthService.getInstructionOrderBirths();
                    List<ColumnHeader> headers = new ArrayList<>();

                    for (Instruction instr : instructions) headers.add(new ColumnHeader(instr.getIdInstruction(), instr.getNameInstruction()));
                    return new  DataResult(rows, headers);
                }
                return new DataResult(new ArrayList<>(), new ArrayList<>());
            }
        };

        task.setOnSucceeded(event -> {
            DataResult result = task.getValue();
            buildTable(result.rows(), result.columnHeaders());
            DataCache.getInstance().put(selected, result.rows(), result.columnHeaders());
            loadingStage.close();
        });
        task.setOnFailed(event -> {
           loadingStage.close();
           Throwable throwable = task.getException();
           String message;
           if (throwable == null) {
               message = "Error al cargar datos.";
           } else {
               message = throwable.getMessage();
           }
           DialogUtil.showErrorDialog("ModelData", "Error al cargar datos, vuelva a intentar.");
        });
        Thread thread = new Thread(task, "data-load-thread");
        thread.setDaemon(true);
        thread.start();

    }

    public void OpenTableData(SortEvent<TableView> tableViewSortEvent) { }

    public void btnClearTable(ActionEvent actionEvent) {
        // TABLA ACTUAL
        tableData.getItems().clear();
        tableData.getColumns().clear();
        // TABLA EN CACHÉ
        DataCache dataCache = DataCache.getInstance();
        dataCache.clearCache();
    }
}