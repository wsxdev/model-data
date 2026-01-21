package com.app.controllers.panels.sidebar;

import com.app.models.entities.BirthRegistration;
import com.app.models.entities.Instruction;
import com.app.models.entities.Mother;
import com.app.models.entities.Province;
import com.app.models.services.BirthService;
import com.app.utils.DialogUtil;
import com.app.utils.LanguageManagerUtil;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.util.StringConverter;

import java.sql.Date;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

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
    private ComboBox<String> cbBirthType;
    @FXML
    private VBox vboxSexContainer;
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
    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<BirthRegistration> tvBirths;
    @FXML
    private TableColumn<BirthRegistration, String> colMotherId;
    @FXML
    private TableColumn<BirthRegistration, String> colBirthType;
    @FXML
    private TableColumn<BirthRegistration, String> colMotherName;
    @FXML
    private TableColumn<BirthRegistration, Integer> colMotherAge;
    @FXML
    private TableColumn<BirthRegistration, String> colMotherCivilStatus;
    @FXML
    private TableColumn<BirthRegistration, String> colMotherInstruction;
    @FXML
    private TableColumn<BirthRegistration, String> colProvince;
    @FXML
    private TableColumn<BirthRegistration, Date> colDate;
    @FXML
    private TableColumn<BirthRegistration, String> colSex;

    private final BirthService birthService = new BirthService();
    private BirthRegistration selectedRegistration;
    private FilteredList<BirthRegistration> filteredData;

    @FXML
    public void initialize() {
        try {
            setupComboBoxes();
            setupTable();
            loadRegistrations();
            setupSearch();
        } catch (Exception e) {
            System.err.println("Error en inicialización: " + e.getMessage());
            e.printStackTrace();
            if (statusLabel != null) {
                ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();
                statusLabel.setText(bundle.getString("registro.status.loadingError"));
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void setupComboBoxes() {
        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();

        // Civil Status
        cbCivilStatus.setItems(FXCollections.observableArrayList(
                "civilStatus.soltera", "civilStatus.casada", "civilStatus.divorciada", "civilStatus.viuda",
                "civilStatus.unionLibre"));
        cbCivilStatus.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String s) {
                return s == null ? "" : bundle.getString(s);
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });

        // Birth Type
        cbBirthType.setItems(FXCollections.observableArrayList(
                "birthType.simple", "birthType.doble", "birthType.triple", "birthType.cuadruple",
                "birthType.quintuple"));
        cbBirthType.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String s) {
                return s == null ? "" : bundle.getString(s);
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });

        cbBirthType.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSexFields(newVal);
        });

        List<Province> provinces = birthService.getAllProvinces();
        cbProvince.setItems(FXCollections.observableArrayList(provinces));
        setComboBoxCustomCell(cbProvince, Province::getNameProvince);

        List<Instruction> instructions = birthService.getAllInstructions();
        cbInstruction.setItems(FXCollections.observableArrayList(instructions));
        setComboBoxCustomCell(cbInstruction, Instruction::getNameInstruction);

        if (statusLabel != null) {
            statusLabel.setText(MessageFormat.format(bundle.getString("registro.status.catalogLoaded"),
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
        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();
        colMotherId
                .setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getMother().getIdentification()));
        colMotherName.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getMother().getNames()));
        colMotherAge.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getMother().getAge()));

        colMotherCivilStatus.setCellValueFactory(cd -> {
            String status = cd.getValue().getMother().getCivilStatus();
            // Try to localize if it looks like a key or match hardcoded strings
            try {
                if (status != null) {
                    if (status.equals("Soltera"))
                        return new ReadOnlyObjectWrapper<>(bundle.getString("civilStatus.soltera"));
                    if (status.equals("Casada"))
                        return new ReadOnlyObjectWrapper<>(bundle.getString("civilStatus.casada"));
                    if (status.equals("Divorciada"))
                        return new ReadOnlyObjectWrapper<>(bundle.getString("civilStatus.divorciada"));
                    if (status.equals("Viuda"))
                        return new ReadOnlyObjectWrapper<>(bundle.getString("civilStatus.viuda"));
                    if (status.equals("Unión Libre"))
                        return new ReadOnlyObjectWrapper<>(bundle.getString("civilStatus.unionLibre"));
                    return new ReadOnlyObjectWrapper<>(bundle.getString(status));
                }
            } catch (Exception e) {
            }
            return new ReadOnlyObjectWrapper<>(status);
        });

        colMotherInstruction.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(
                cd.getValue().getInstruction() != null ? cd.getValue().getInstruction().getNameInstruction() : "N/A"));
        colProvince
                .setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getProvince().getNameProvince()));
        colDate.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getBirthDate()));

        colSex.setCellValueFactory(cd -> {
            String s = cd.getValue().getSex();
            if ("Masculino".equalsIgnoreCase(s))
                return new ReadOnlyObjectWrapper<>(bundle.getString("sex.masculino"));
            if ("Femenino".equalsIgnoreCase(s))
                return new ReadOnlyObjectWrapper<>(bundle.getString("sex.femenino"));
            return new ReadOnlyObjectWrapper<>(s);
        });

        colBirthType.setCellValueFactory(cd -> {
            String t = cd.getValue().getBirthType();
            if ("Simple".equalsIgnoreCase(t))
                return new ReadOnlyObjectWrapper<>(bundle.getString("birthType.simple"));
            if ("Doble".equalsIgnoreCase(t))
                return new ReadOnlyObjectWrapper<>(bundle.getString("birthType.doble"));
            if ("Triple".equalsIgnoreCase(t))
                return new ReadOnlyObjectWrapper<>(bundle.getString("birthType.triple"));
            if ("Cuádruple".equalsIgnoreCase(t))
                return new ReadOnlyObjectWrapper<>(bundle.getString("birthType.cuadruple"));
            if ("Quíntuple".equalsIgnoreCase(t))
                return new ReadOnlyObjectWrapper<>(bundle.getString("birthType.quintuple"));
            return new ReadOnlyObjectWrapper<>(t);
        });

        tvBirths.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });
    }

    private void loadRegistrations() {
        List<BirthRegistration> list = birthService.getAllBirthRegistrations();
        filteredData = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);
        tvBirths.setItems(filteredData);
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(birth -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return birth.getMother().getIdentification().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void fillForm(BirthRegistration br) {
        this.selectedRegistration = br;
        Mother m = br.getMother();
        txtMotherId.setText(m.getIdentification());
        txtMotherNames.setText(m.getNames());
        txtMotherAge.setText(String.valueOf(m.getAge()));

        // Map back to keys for ComboBox
        String cs = m.getCivilStatus();
        if ("Soltera".equals(cs))
            cbCivilStatus.setValue("civilStatus.soltera");
        else if ("Casada".equals(cs))
            cbCivilStatus.setValue("civilStatus.casada");
        else if ("Divorciada".equals(cs))
            cbCivilStatus.setValue("civilStatus.divorciada");
        else if ("Viuda".equals(cs))
            cbCivilStatus.setValue("civilStatus.viuda");
        else if ("Unión Libre".equals(cs))
            cbCivilStatus.setValue("civilStatus.unionLibre");
        else
            cbCivilStatus.setValue(cs);

        cbProvince.setValue(br.getProvince());
        cbInstruction.setValue(br.getInstruction());

        String bt = br.getBirthType();
        if ("Simple".equalsIgnoreCase(bt))
            cbBirthType.setValue("birthType.simple");
        else if ("Doble".equalsIgnoreCase(bt))
            cbBirthType.setValue("birthType.doble");
        else if ("Triple".equalsIgnoreCase(bt))
            cbBirthType.setValue("birthType.triple");
        else if ("Cuádruple".equalsIgnoreCase(bt))
            cbBirthType.setValue("birthType.cuadruple");
        else if ("Quíntuple".equalsIgnoreCase(bt))
            cbBirthType.setValue("birthType.quintuple");
        else
            cbBirthType.setValue(bt);

        updateSexFields(cbBirthType.getValue());

        // Set sex for the single selected record (first combo)
        if (!vboxSexContainer.getChildren().isEmpty()) {
            @SuppressWarnings("unchecked")
            ComboBox<String> firstSex = (ComboBox<String>) ((VBox) vboxSexContainer.getChildren().get(0)).getChildren()
                    .get(1);
            String s = br.getSex();
            if ("Masculino".equalsIgnoreCase(s))
                firstSex.setValue("sex.masculino");
            else if ("Femenino".equalsIgnoreCase(s))
                firstSex.setValue("sex.femenino");
            else
                firstSex.setValue(s);
        }

        if (br.getBirthDate() != null) {
            dpBirthDate.setValue(br.getBirthDate().toLocalDate());
        }

        btnRegister.setDisable(true);
        btnUpdate.setDisable(false);
        btnDelete.setDisable(false);
    }

    private void updateSexFields(String birthTypeKey) {
        vboxSexContainer.getChildren().clear();
        if (birthTypeKey == null)
            return;

        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();

        int count = 1;
        if ("birthType.doble".equals(birthTypeKey))
            count = 2;
        else if ("birthType.triple".equals(birthTypeKey))
            count = 3;
        else if ("birthType.cuadruple".equals(birthTypeKey))
            count = 4;
        else if ("birthType.quintuple".equals(birthTypeKey))
            count = 5;

        for (int i = 1; i <= count; i++) {
            VBox sexBox = new VBox(5);
            String labelText = count > 1
                    ? MessageFormat.format(bundle.getString("registro.label.babySex"), i)
                    : bundle.getString("registro.label.babySexSingle");

            Label label = new Label(labelText);
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: -primary-color;");

            ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList("sex.masculino", "sex.femenino"));
            combo.setConverter(new StringConverter<String>() {
                @Override
                public String toString(String s) {
                    return s == null ? "" : bundle.getString(s);
                }

                @Override
                public String fromString(String string) {
                    return null;
                }
            });
            combo.setPromptText(bundle.getString("registro.prompt.sex"));
            combo.setMaxWidth(Double.MAX_VALUE);
            sexBox.getChildren().addAll(label, combo);
            vboxSexContainer.getChildren().add(sexBox);
        }
    }

    @FXML
    public void onRegister() {
        if (!validateInput())
            return;

        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();
        try {
            Mother mother = getMotherFromForm();
            Date date = Date.valueOf(dpBirthDate.getValue());
            Province province = cbProvince.getValue();
            Instruction instruction = cbInstruction.getValue();

            // Map key back to storage value (Spanish for DB compatibility if needed, but DB
            // should ideally use IDs/Codes)
            String birthTypeKey = cbBirthType.getValue();
            String birthType = "Simple";
            if ("birthType.doble".equals(birthTypeKey))
                birthType = "Doble";
            else if ("birthType.triple".equals(birthTypeKey))
                birthType = "Triple";
            else if ("birthType.cuadruple".equals(birthTypeKey))
                birthType = "Cuádruple";
            else if ("birthType.quintuple".equals(birthTypeKey))
                birthType = "Quíntuple";

            // Register each baby as an individual birth
            for (javafx.scene.Node node : vboxSexContainer.getChildren()) {
                if (node instanceof VBox) {
                    @SuppressWarnings("unchecked")
                    ComboBox<String> combo = (ComboBox<String>) ((VBox) node).getChildren().get(1);
                    String sexKey = combo.getValue();
                    String sex = "Masculino";
                    if ("sex.femenino".equals(sexKey))
                        sex = "Femenino";

                    birthService.registerBirth(mother, province, instruction, date, sex, birthType);
                }
            }

            showMessage(bundle.getString("registro.status.success"), "green");
            loadRegistrations();
            clearForm();
        } catch (Exception e) {
            showMessage(bundle.getString("registro.status.loadingError") + ": " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    @FXML
    public void onUpdate() {
        if (selectedRegistration == null)
            return;
        if (!validateInput())
            return;

        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();
        try {
            Mother mother = getMotherFromForm();
            mother.setIdMother(selectedRegistration.getMother().getIdMother()); // Keep ID

            selectedRegistration.setMother(mother);
            selectedRegistration.setProvince(cbProvince.getValue());
            selectedRegistration.setInstruction(cbInstruction.getValue());
            selectedRegistration.setBirthDate(Date.valueOf(dpBirthDate.getValue()));

            // Update sex from first combo
            if (!vboxSexContainer.getChildren().isEmpty()) {
                @SuppressWarnings("unchecked")
                ComboBox<String> firstSexComp = (ComboBox<String>) ((VBox) vboxSexContainer.getChildren().get(0))
                        .getChildren().get(1);
                String sexKey = firstSexComp.getValue();
                selectedRegistration.setSex("sex.femenino".equals(sexKey) ? "Femenino" : "Masculino");
            }

            String btKey = cbBirthType.getValue();
            String bt = "Simple";
            if ("birthType.doble".equals(btKey))
                bt = "Doble";
            else if ("birthType.triple".equals(btKey))
                bt = "Triple";
            else if ("birthType.cuadruple".equals(btKey))
                bt = "Cuádruple";
            else if ("birthType.quintuple".equals(btKey))
                bt = "Quíntuple";
            selectedRegistration.setBirthType(bt);

            birthService.updateBirthRegistration(selectedRegistration);

            showMessage(bundle.getString("registro.status.updateSuccess"), "green");
            loadRegistrations();
            clearForm();
        } catch (Exception e) {
            showMessage(bundle.getString("registro.status.loadingError") + ": " + e.getMessage(), "red");
        }
    }

    @FXML
    public void onDelete() {
        if (selectedRegistration == null)
            return;

        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("registro.delete.confirm.title"));
        alert.setHeaderText(null);
        alert.setContentText(bundle.getString("registro.delete.confirm.content"));

        java.util.Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                birthService.deleteBirthRegistration(selectedRegistration.getIdBirthRegistration());
                showMessage(bundle.getString("registro.status.deleteSuccess"), "blue");
                loadRegistrations();
                clearForm();
            } catch (Exception e) {
                showMessage(bundle.getString("registro.status.loadingError") + ": " + e.getMessage(), "red");
            }
        }
    }

    private boolean validateInput() {
        ResourceBundle bundle = LanguageManagerUtil.getInstance().getBundle();
        String motherId = txtMotherId.getText().trim();
        String motherNames = txtMotherNames.getText().trim();
        String ageText = txtMotherAge.getText().trim();

        if (motherId.isEmpty() || motherNames.isEmpty() || ageText.isEmpty() ||
                cbCivilStatus.getValue() == null || cbBirthType.getValue() == null ||
                cbProvince.getValue() == null || cbInstruction.getValue() == null ||
                dpBirthDate.getValue() == null) {
            showMessage(bundle.getString("registro.status.validationError"), "red");
            return false;
        }

        // Validate all sex fields
        for (javafx.scene.Node node : vboxSexContainer.getChildren()) {
            if (node instanceof VBox) {
                @SuppressWarnings("unchecked")
                ComboBox<String> combo = (ComboBox<String>) ((VBox) node).getChildren().get(1);
                if (combo.getValue() == null) {
                    showMessage(bundle.getString("registro.status.sexValidationError"), "red");
                    return false;
                }
            }
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age < 10 || age > 100)
                throw new Exception();
        } catch (Exception e) {
            showMessage(bundle.getString("registro.status.ageValidationError"), "red");
            return false;
        }
        return true;
    }

    private Mother getMotherFromForm() {
        String civilStatusKey = cbCivilStatus.getValue();
        String civilStatus = "Soltera";
        if ("civilStatus.casada".equals(civilStatusKey))
            civilStatus = "Casada";
        else if ("civilStatus.divorciada".equals(civilStatusKey))
            civilStatus = "Divorciada";
        else if ("civilStatus.viuda".equals(civilStatusKey))
            civilStatus = "Viuda";
        else if ("civilStatus.unionLibre".equals(civilStatusKey))
            civilStatus = "Unión Libre";

        return new Mother(
                txtMotherId.getText().trim(),
                txtMotherNames.getText().trim(),
                Integer.parseInt(txtMotherAge.getText().trim()),
                civilStatus);
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
        cbBirthType.getSelectionModel().clearSelection();
        vboxSexContainer.getChildren().clear();
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
