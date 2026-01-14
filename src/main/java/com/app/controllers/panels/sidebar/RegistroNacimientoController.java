package com.app.controllers.panels.sidebar;

import com.app.models.entities.BirthRegistration;
import com.app.models.entities.Instruction;
import com.app.models.entities.Mother;
import com.app.models.entities.Province;
import com.app.models.services.BirthService;
import com.app.utils.DialogUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class RegistroNacimientoController {

    @FXML
    private TextField txtMotherId;
    @FXML
    private TextField txtMotherNames;
    @FXML
    private TextField txtMotherAge;
    @FXML
    private ComboBox<String> cbCivilStatus;

    @FXML
    private ComboBox<Province> cbProvince;
    @FXML
    private ComboBox<Instruction> cbInstruction;

    @FXML
    private ComboBox<String> cbSex;
    @FXML
    private ComboBox<String> cbBirthType;
    @FXML
    private DatePicker dpBirthDate;

    @FXML
    private Button btnRegister;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Label statusLabel;

    // Table
    @FXML
    private TableView<BirthRegistration> tvBirths;
    @FXML
    private TableColumn<BirthRegistration, Integer> colId;
    @FXML
    private TableColumn<BirthRegistration, String> colMotherId;
    @FXML
    private TableColumn<BirthRegistration, String> colProvince;
    @FXML
    private TableColumn<BirthRegistration, Date> colDate;
    @FXML
    private TableColumn<BirthRegistration, String> colSex;

    private final BirthService birthService = new BirthService();
    private BirthRegistration selectedRegistration;

    @FXML
    public void initialize() {
        try {
            setupComboBoxes();
            setupTable();
            loadRegistrations();
        } catch (Exception e) {
            System.err.println("Error en inicialización: " + e.getMessage());
            e.printStackTrace();
            if (statusLabel != null) {
                statusLabel.setText("Error al cargar datos. Ver consola.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void setupComboBoxes() {
        cbCivilStatus.setItems(FXCollections.observableArrayList(
                "Soltera", "Casada", "Divorciada", "Viuda", "Unión Libre"));
        cbSex.setItems(FXCollections.observableArrayList("Masculino", "Femenino"));
        cbBirthType.setItems(FXCollections.observableArrayList("Simple", "Doble", "Triple", "Cuádruple", "Quíntuple"));

        List<Province> provinces = birthService.getAllProvinces();
        System.out.println("DEBUG: Cargadas " + provinces.size() + " provincias.");
        cbProvince.setItems(FXCollections.observableArrayList(provinces));
        setComboBoxCustomCell(cbProvince, Province::getNameProvince);

        List<Instruction> instructions = birthService.getAllInstructions();
        System.out.println("DEBUG: Cargadas " + instructions.size() + " instrucciones.");
        cbInstruction.setItems(FXCollections.observableArrayList(instructions));
        setComboBoxCustomCell(cbInstruction, Instruction::getNameInstruction);

        if (statusLabel != null) {
            statusLabel.setText(String.format("Catálogos cargados: %d provincias, %d instrucciones.",
                    provinces.size(), instructions.size()));
            statusLabel.setStyle("-fx-text-fill: gray;");
        }
    }

    private <T> void setComboBoxCustomCell(ComboBox<T> combo, java.util.function.Function<T, String> mapper) {
        combo.setCellFactory(param -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : mapper.apply(item));
            }
        });
        combo.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : mapper.apply(item));
            }
        });
    }

    private void setupTable() {
        colId.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getIdBirthRegistration()));
        colMotherId
                .setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getMother().getIdentification()));
        colProvince
                .setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getProvince().getNameProvince()));
        colDate.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getBirthDate()));
        colSex.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getSex()));

        tvBirths.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });
    }

    private void loadRegistrations() {
        List<BirthRegistration> list = birthService.getAllBirthRegistrations();
        tvBirths.setItems(FXCollections.observableArrayList(list));
    }

    private void fillForm(BirthRegistration br) {
        this.selectedRegistration = br;
        Mother m = br.getMother();
        txtMotherId.setText(m.getIdentification());
        txtMotherNames.setText(m.getNames());
        txtMotherAge.setText(String.valueOf(m.getAge()));
        cbCivilStatus.setValue(m.getCivilStatus());

        cbProvince.setValue(br.getProvince());
        cbInstruction.setValue(br.getInstruction());
        cbSex.setValue(br.getSex());
        cbBirthType.setValue(br.getBirthType());

        if (br.getBirthDate() != null) {
            dpBirthDate.setValue(br.getBirthDate().toLocalDate());
        }

        btnRegister.setDisable(true);
        btnUpdate.setDisable(false);
        btnDelete.setDisable(false);
    }

    @FXML
    public void onRegister() {
        if (!validateInput())
            return;

        try {
            Mother mother = getMotherFromForm();
            Date date = Date.valueOf(dpBirthDate.getValue());
            birthService.registerBirth(mother, cbProvince.getValue(), cbInstruction.getValue(), date, cbSex.getValue(),
                    cbBirthType.getValue());

            showMessage("Nacimiento registrado exitosamente.", "green");
            loadRegistrations();
            clearForm();
        } catch (Exception e) {
            showMessage("Error al registrar: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    @FXML
    public void onUpdate() {
        if (selectedRegistration == null)
            return;
        if (!validateInput())
            return;

        try {
            Mother mother = getMotherFromForm();
            mother.setIdMother(selectedRegistration.getMother().getIdMother()); // Keep ID

            selectedRegistration.setMother(mother);
            selectedRegistration.setProvince(cbProvince.getValue());
            selectedRegistration.setInstruction(cbInstruction.getValue());
            selectedRegistration.setBirthDate(Date.valueOf(dpBirthDate.getValue()));
            selectedRegistration.setSex(cbSex.getValue());
            selectedRegistration.setBirthType(cbBirthType.getValue());

            birthService.updateBirthRegistration(selectedRegistration);

            showMessage("Registro actualizado exitosamente.", "green");
            loadRegistrations();
            clearForm();
        } catch (Exception e) {
            showMessage("Error al actualizar: " + e.getMessage(), "red");
        }
    }

    @FXML
    public void onDelete() {
        if (selectedRegistration == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro que desea eliminar este registro? Esta acción no se puede deshacer.");

        java.util.Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                birthService.deleteBirthRegistration(selectedRegistration.getIdBirthRegistration());
                showMessage("Registro eliminado.", "blue");
                loadRegistrations();
                clearForm();
            } catch (Exception e) {
                showMessage("Error al eliminar: " + e.getMessage(), "red");
            }
        }
    }

    private boolean validateInput() {
        String motherId = txtMotherId.getText().trim();
        String motherNames = txtMotherNames.getText().trim();
        String ageText = txtMotherAge.getText().trim();

        if (motherId.isEmpty() || motherNames.isEmpty() || ageText.isEmpty() ||
                cbCivilStatus.getValue() == null || cbSex.getValue() == null ||
                cbBirthType.getValue() == null || cbProvince.getValue() == null ||
                cbInstruction.getValue() == null || dpBirthDate.getValue() == null) {
            showMessage("Por favor, complete todos los campos.", "red");
            return false;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age < 10 || age > 100)
                throw new Exception();
        } catch (Exception e) {
            showMessage("Edad inválida. Ingrese un número entre 10 y 100.", "red");
            return false;
        }
        return true;
    }

    private Mother getMotherFromForm() {
        return new Mother(
                txtMotherId.getText().trim(),
                txtMotherNames.getText().trim(),
                Integer.parseInt(txtMotherAge.getText().trim()),
                cbCivilStatus.getValue());
    }

    private void showMessage(String msg, String color) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    public void clearForm() {
        txtMotherId.clear();
        txtMotherNames.clear();
        txtMotherAge.clear();
        cbCivilStatus.getSelectionModel().clearSelection();
        cbSex.getSelectionModel().clearSelection();
        cbBirthType.getSelectionModel().clearSelection();
        cbProvince.getSelectionModel().clearSelection();
        cbInstruction.getSelectionModel().clearSelection();
        dpBirthDate.setValue(null);
        statusLabel.setText("");

        selectedRegistration = null;
        btnRegister.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tvBirths.getSelectionModel().clearSelection();
    }
}
