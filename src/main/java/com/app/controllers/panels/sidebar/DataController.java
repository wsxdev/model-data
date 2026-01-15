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

import javax.swing.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class DataController implements Initializable {

    @FXML
    private TableView tableData;
    @FXML
    private ComboBox<String> datosAnalizarComboBox;
    private ResourceBundle william;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (datosAnalizarComboBox.getItems() == null || datosAnalizarComboBox.getItems().isEmpty()) {
            ObservableList<String> datosComboBox = FXCollections.observableArrayList(
                    "Provincia - año",
                    "Instrucción - año",
                    "Registros de Nacimientos");
            datosAnalizarComboBox.setItems(datosComboBox);
        }

        william = resourceBundle;
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
            DialogUtil.showInformationDialog("ModelData", william.getString("dialog.popup.dataController.information"));
            return;
        }
        loadData(selected, true);
    }

    private void buildTable(List<?> rows, List<ColumnHeader> headers) {
        tableData.getColumns().clear();

        if (headers == null || headers.isEmpty()) {
            // Case for BirthRegistration (Individual records)
            setupBirthRegistrationColumns();
        } else {
            // Case for Aggregated data
            TableColumn<Object, Integer> columnYear = new TableColumn<>("Año");
            columnYear.setCellValueFactory(cellData -> {
                if (cellData.getValue() instanceof YearDataSummary summary) {
                    return new ReadOnlyObjectWrapper<>(summary.year());
                }
                return null;
            });
            tableData.getColumns().add(columnYear);

            for (ColumnHeader header : headers) {
                String id = header.id();
                TableColumn<Object, Integer> columnColumnYear = new TableColumn<>(header.name());
                columnColumnYear.setCellValueFactory(cellData -> {
                    if (cellData.getValue() instanceof YearDataSummary summary) {
                        return new ReadOnlyObjectWrapper<>(summary.getCountForRecord(id));
                    }
                    return null;
                });
                tableData.getColumns().add(columnColumnYear);
            }
        }

        ObservableList<Object> items = FXCollections.observableArrayList();
        for (Object row : rows) {
            items.add(row);
        }
        tableData.setItems(items);
    }

    private void setupBirthRegistrationColumns() {
        TableColumn<Object, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cd -> {
            if (cd.getValue() instanceof com.app.models.entities.BirthRegistration br)
                return new ReadOnlyObjectWrapper<>(br.getIdBirthRegistration());
            return null;
        });

        TableColumn<Object, String> colMother = new TableColumn<>("Madre (ID)");
        colMother.setCellValueFactory(cd -> {
            if (cd.getValue() instanceof com.app.models.entities.BirthRegistration br)
                return new ReadOnlyObjectWrapper<>(br.getMother().getIdentification());
            return null;
        });

        TableColumn<Object, String> colProv = new TableColumn<>("Provincia");
        colProv.setCellValueFactory(cd -> {
            if (cd.getValue() instanceof com.app.models.entities.BirthRegistration br)
                return new ReadOnlyObjectWrapper<>(br.getProvince().getNameProvince());
            return null;
        });

        TableColumn<Object, String> colInsn = new TableColumn<>("Instrucción");
        colInsn.setCellValueFactory(cd -> {
            if (cd.getValue() instanceof com.app.models.entities.BirthRegistration br)
                return new ReadOnlyObjectWrapper<>(br.getInstruction().getNameInstruction());
            return null;
        });

        TableColumn<Object, java.sql.Date> colDate = new TableColumn<>("Fecha");
        colDate.setCellValueFactory(cd -> {
            if (cd.getValue() instanceof com.app.models.entities.BirthRegistration br)
                return new ReadOnlyObjectWrapper<>(br.getBirthDate());
            return null;
        });

        TableColumn<Object, String> colSex = new TableColumn<>("Sexo");
        colSex.setCellValueFactory(cd -> {
            if (cd.getValue() instanceof com.app.models.entities.BirthRegistration br)
                return new ReadOnlyObjectWrapper<>(br.getSex());
            return null;
        });

        TableColumn<Object, String> colType = new TableColumn<>("Tipo Parto");
        colType.setCellValueFactory(cd -> {
            if (cd.getValue() instanceof com.app.models.entities.BirthRegistration br)
                return new ReadOnlyObjectWrapper<>(br.getBirthType());
            return null;
        });

        tableData.getColumns().addAll(colId, colMother, colProv, colInsn, colDate, colSex, colType);
    }

    private void loadData(String selected, boolean forceUpdate) {
        DataCache cache = DataCache.getInstance();
        //
        if (!forceUpdate && cache.hasCache() && selected.equals(cache.getLastSelection())) {
            buildTable(cache.getLastRows(), cache.getLastHeaders());
            return;
        }

        Stage loadingStage = DialogUtil.showLoadingDialog("ModelData",
                "Cargando datos... Cálmese, no sea impaciente. >:(");
        Task<DataResult> task = new Task<>() {
            @Override
            protected DataResult call() throws Exception {
                BirthService birthService = new BirthService();
                if ("Provincia - año".equals(selected)) {
                    List<YearDataSummary> rows = birthService.getPivotByYear();
                    List<Province> provinces = birthService.getProvinceOrderBirths();
                    List<ColumnHeader> headers = new ArrayList<>();

                    for (Province prov : provinces)
                        headers.add(new ColumnHeader(prov.getIdProvince(), prov.getNameProvince()));
                    return new DataResult(rows, headers);
                }
                if ("Instrucción - año".equals(selected)) {
                    List<YearDataSummary> rows = birthService.getPivotYearInstruction();
                    List<Instruction> instructions = birthService.getInstructionOrderBirths();
                    List<ColumnHeader> headers = new ArrayList<>();

                    for (Instruction instr : instructions)
                        headers.add(new ColumnHeader(instr.getIdInstruction(), instr.getNameInstruction()));
                    return new DataResult(rows, headers);
                }
                if ("Registros de Nacimientos".equals(selected)) {
                    List<com.app.models.entities.BirthRegistration> rows = birthService.getAllBirthRegistrations();
                    return new DataResult(rows, new ArrayList<>());
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

    public void OpenTableData(SortEvent<TableView> tableViewSortEvent) {
    }

    public void btnClearTable(ActionEvent actionEvent) {
        if (tableData.getItems().isEmpty()) {
            DialogUtil.showInformationDialog("ModelData", "La tabla está vacía. No hay datos para limpiar.");
        } else {
            // TABLA ACTUAL
            tableData.getItems().clear();
            tableData.getColumns().clear();
            // TABLA EN CACHÉ
            DataCache dataCache = DataCache.getInstance();
            dataCache.clearCache();
            DialogUtil.showInformationDialog("ModelData",
                    "Los datos de la tabla se limpiaron correctamente. Tenga buen día/tarde/noche.");
        }

    }
}