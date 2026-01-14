package com.app.controllers.panels.sidebar;

import com.app.models.entities.Instruction;
import com.app.models.entities.Mother;
import com.app.models.entities.Province;
import com.app.models.services.BirthService;
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
    private ComboBox<Province> cbProvince;
    @FXML
    private ComboBox<Instruction> cbInstruction;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private Label statusLabel;
    @FXML
    private Button btnRegister;

    private final BirthService birthService = new BirthService();

    @FXML
    private TextField txtMotherNames;
    @FXML
    private TextField txtMotherAge;
    @FXML
    private ComboBox<String> cbCivilStatus;
    @FXML
    private ComboBox<String> cbSex;
    @FXML
    private ComboBox<String> cbBirthType;

    @FXML
    public void initialize() {
        loadComboBoxes();
        cbCivilStatus.setItems(FXCollections.observableArrayList(
                "Soltera", "Casada", "Divorciada", "Viuda", "Unión Libre"));
        cbSex.setItems(FXCollections.observableArrayList("Masculino", "Femenino"));
        cbBirthType.setItems(FXCollections.observableArrayList("Simple", "Doble", "Triple", "Cuádruple", "Quíntuple"));
    }

    private void loadComboBoxes() {
        List<Province> provinces = birthService.getAllProvinces();
        cbProvince.setItems(FXCollections.observableArrayList(provinces));

        // Custom cell factory for Provinces to show name
        cbProvince.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Province item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNameProvince());
            }
        });
        cbProvince.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Province item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNameProvince());
            }
        });

        List<Instruction> instructions = birthService.getAllInstructions();
        cbInstruction.setItems(FXCollections.observableArrayList(instructions));

        // Custom cell factory for Instructions
        cbInstruction.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Instruction item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNameInstruction());
            }
        });
        cbInstruction.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Instruction item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNameInstruction());
            }
        });
    }

    @FXML
    public void onRegister() {
        statusLabel.setText("");
        statusLabel.setStyle("-fx-text-fill: black;");

        String motherId = txtMotherId.getText().trim();
        String motherNames = txtMotherNames.getText().trim();
        String ageText = txtMotherAge.getText().trim();
        String civilStatus = cbCivilStatus.getValue();
        String sex = cbSex.getValue();
        String birthType = cbBirthType.getValue();

        Province selectedProvince = cbProvince.getValue();
        Instruction selectedInstruction = cbInstruction.getValue();
        LocalDate localDate = dpBirthDate.getValue();

        if (motherId.isEmpty() || motherNames.isEmpty() || ageText.isEmpty() || civilStatus == null ||
                sex == null || birthType == null ||
                selectedProvince == null || selectedInstruction == null || localDate == null) {
            statusLabel.setText("Por favor, complete todos los campos.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age < 10 || age > 100)
                throw new NumberFormatException("Edad inválida");
        } catch (NumberFormatException e) {
            statusLabel.setText("Edad inválida. Ingrese un número entre 10 y 100.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            Mother mother = new Mother(motherId, motherNames, age, civilStatus);
            Date date = Date.valueOf(localDate);

            birthService.registerBirth(mother, selectedProvince, selectedInstruction, date, sex, birthType);

            statusLabel.setText("Nacimiento registrado exitosamente.");
            statusLabel.setStyle("-fx-text-fill: green;");
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error al registrar: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtMotherId.clear();
        txtMotherNames.clear();
        txtMotherAge.clear();
        cbCivilStatus.getSelectionModel().clearSelection();
        cbSex.getSelectionModel().clearSelection();
        cbBirthType.getSelectionModel().clearSelection();
        cbProvince.getSelectionModel().clearSelection();
        cbInstruction.getSelectionModel().clearSelection();
        dpBirthDate.setValue(null);
    }
}
