package com.app.controllers.panels.sidebar;

import com.app.models.modeling.*;
import com.app.models.modeling.TipoSegmentacion;
import com.app.models.services.ModelingService;
import com.app.models.services.BirthService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.CheckComboBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controlador para el módulo de Modelado Matemático EDO.
 * Refactorizado para validaciones estrictas y visualización científica.
 */
public class ModeladoController {

    public enum ModoComparacion {
        INDIVIDUAL("modelado.compare.mode.individual"),
        COMPARACION_1_VS_1("modelado.compare.mode.1vs1"),
        COMPARACION_1_VS_N("modelado.compare.mode.1vsn"),
        COMPARACION_1_VS_TODOS("modelado.compare.mode.1vsall");

        private final String key;

        ModoComparacion(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    // --- FXML Controls ---
    @FXML
    private ComboBox<TipoSegmentacion> comboTipoSegmentacion;
    @FXML
    private ComboBox<ModoComparacion> comboModoComparacion;
    @FXML
    private Label lblInfoComparacion; // Ayuda contextual nueva

    @FXML
    private ComboBox<String> comboCategoria; // Base
    @FXML
    private HBox boxCategoria2;
    @FXML
    private ComboBox<String> comboCategoria2; // Comparación

    @FXML
    private HBox boxCategoriaMulti;
    @FXML
    private CheckComboBox<String> checkComboCategoriaMulti; // Multi

    // Rango Temporal
    @FXML
    private ComboBox<Integer> comboAnioInicial;
    @FXML
    private ComboBox<Integer> comboAnioFinal;
    @FXML
    private Spinner<Integer> spinnerIntervalo;

    // Actions
    @FXML
    private Button btnModelar;
    @FXML
    private Button btnGraficar;
    @FXML
    private Button btnLimpiar;

    // Resultados
    @FXML
    private VBox panelResultados;
    @FXML
    private VBox boxMetricas; // Contenedor de tabla
    @FXML
    private StackPane contenedorGrafica;
    @FXML
    private LineChart<Number, Number> chartModelado;

    // Tabla y Análisis
    @FXML
    private TableView<ResultadoModeladoEDO> tblAnalisis;
    @FXML
    private TableColumn<ResultadoModeladoEDO, String> colCategoria;
    @FXML
    private TableColumn<ResultadoModeladoEDO, String> colEcuacion;
    @FXML
    private TableColumn<ResultadoModeladoEDO, String> colParametroA;
    @FXML
    private TableColumn<ResultadoModeladoEDO, String> colParametroB;
    @FXML
    private TableColumn<ResultadoModeladoEDO, String> colR2;
    @FXML
    private TableColumn<ResultadoModeladoEDO, String> colMAE;
    @FXML
    private TableColumn<ResultadoModeladoEDO, String> colRMSE;
    @FXML
    private TableColumn<ResultadoModeladoEDO, String> colInterpretacion;
    @FXML
    private Label lblAnalisisTexto;

    // Servicios y Datos
    private ModelingService modelingService;
    private BirthService birthService;
    private ObservableList<ResultadoModeladoEDO> resultadosCalculados = FXCollections.observableArrayList();

    // Paleta Científica (ColorBrewer Set1/Dark2 adaptada)
    private final String[] PALETA_COLORES = {
            "#1f77b4", // Azul (Base)
            "#d62728", // Rojo
            "#2ca02c", // Verde
            "#9467bd", // Púrpura
            "#ff7f0e", // Naranja
            "#8c564b", // Marrón
            "#e377c2", // Rosa
            "#7f7f7f", // Gris
            "#bcbd22", // Lima
            "#17becf" // Turquesa
    };

    @FXML
    public void initialize() {
        modelingService = new ModelingService();
        birthService = new BirthService();

        configurarControles();
        configurarTabla();
        configurarListeners();
    }

    private void configurarControles() {
        comboTipoSegmentacion.getItems().addAll(TipoSegmentacion.PROVINCIA, TipoSegmentacion.INSTRUCCION);
        comboTipoSegmentacion.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoSegmentacion t) {
                return t != null ? t.getDisplayName() : "";
            }

            @Override
            public TipoSegmentacion fromString(String s) {
                return null;
            }
        });

        comboModoComparacion.getItems().addAll(ModoComparacion.values());
        comboModoComparacion.setConverter(new StringConverter<>() {
            @Override
            public String toString(ModoComparacion m) {
                if (m == null)
                    return "";
                try {
                    return ResourceBundle.getBundle("i18n/messages").getString(m.getKey());
                } catch (Exception e) {
                    return m.name();
                }
            }

            @Override
            public ModoComparacion fromString(String s) {
                return null;
            }
        });
        comboModoComparacion.setValue(ModoComparacion.INDIVIDUAL);

        // Años 1990-2024
        List<Integer> years = IntStream.rangeClosed(1990, 2024).boxed().collect(Collectors.toList());
        comboAnioInicial.getItems().addAll(years);
        comboAnioFinal.getItems().addAll(years);
        comboAnioInicial.setValue(2000);
        comboAnioFinal.setValue(2020);
        spinnerIntervalo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    private void configurarTabla() {
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().categoria()));
        colEcuacion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEcuacionFormateada()));
        colParametroA.setCellValueFactory(
                data -> new SimpleStringProperty(String.format("%.4e", data.getValue().parametroA())));
        colParametroB.setCellValueFactory(
                data -> new SimpleStringProperty(String.format("%.6f", data.getValue().parametroB())));
        colR2.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.4f", data.getValue().r2())));
        colMAE.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().mae())));
        colRMSE.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().rmse())));
        colInterpretacion.setCellValueFactory(data -> new SimpleStringProperty(interpretarTendencia(data.getValue())));

        tblAnalisis.setItems(resultadosCalculados);
    }

    private void configurarListeners() {
        comboTipoSegmentacion.setOnAction(e -> {
            cargarCategoriasBase();
            actualizarEstadoUI();
        });
        comboModoComparacion.setOnAction(e -> actualizarEstadoUI());
        // Validation 1.1: Al cambiar base, sacarla de la lista de comparación
        comboCategoria.setOnAction(e -> filtrarListasComparacion());
        comboAnioFinal.setOnAction(e -> validarRangoTemporal());
    }

    private void cargarCategoriasBase() {
        comboCategoria.getItems().clear();
        TipoSegmentacion tipo = comboTipoSegmentacion.getValue();
        if (tipo == null)
            return;

        List<String> items = obtenerItemsPorTipo(tipo);
        comboCategoria.getItems().addAll(items);
    }

    private List<String> obtenerItemsPorTipo(TipoSegmentacion tipo) {
        if (tipo == TipoSegmentacion.PROVINCIA) {
            return birthService.getAllProvinces().stream().map(p -> p.getNameProvince()).collect(Collectors.toList());
        } else {
            return birthService.getAllInstructions().stream().map(i -> i.getNameInstruction())
                    .collect(Collectors.toList());
        }
    }

    // Validation 1.1 & 1.2 strict implementation
    private void filtrarListasComparacion() {
        String base = comboCategoria.getValue();
        TipoSegmentacion tipo = comboTipoSegmentacion.getValue();
        if (tipo == null)
            return;

        List<String> todos = obtenerItemsPorTipo(tipo);
        List<String> filtrados = new ArrayList<>(todos);
        if (base != null)
            filtrados.remove(base); // REGLA DE ORO: Base fuera

        // Update Combo 2
        String curr2 = comboCategoria2.getValue();
        comboCategoria2.getItems().setAll(filtrados);
        if (base != null && base.equals(curr2))
            comboCategoria2.setValue(null);

        // Update Multi
        List<String> checked = new ArrayList<>(checkComboCategoriaMulti.getCheckModel().getCheckedItems());
        checkComboCategoriaMulti.getItems().setAll(filtrados);
        // Restore checks only if valid
        for (String s : checked) {
            if (!s.equals(base))
                checkComboCategoriaMulti.getCheckModel().check(s);
        }
    }

    private void actualizarEstadoUI() {
        TipoSegmentacion tipo = comboTipoSegmentacion.getValue();
        ModoComparacion modo = comboModoComparacion.getValue();
        if (tipo == null || modo == null)
            return;

        // Validation 1.3: Instruccion restricted
        if (tipo == TipoSegmentacion.INSTRUCCION
                && (modo == ModoComparacion.COMPARACION_1_VS_N || modo == ModoComparacion.COMPARACION_1_VS_TODOS)) {
            comboModoComparacion.setValue(ModoComparacion.INDIVIDUAL);
            mostrarAlerta("Información",
                    "Por consistencia metodológica, Nivel de Instrucción solo permite comparación individual o par (1 vs 1).");
            return;
        }

        boolean showCat2 = (modo == ModoComparacion.COMPARACION_1_VS_1);
        boolean showMulti = (modo == ModoComparacion.COMPARACION_1_VS_N);

        boxCategoria2.setVisible(showCat2);
        boxCategoria2.setManaged(showCat2);
        boxCategoriaMulti.setVisible(showMulti);
        boxCategoriaMulti.setManaged(showMulti);

        // Help Context Label with ID check
        if (lblInfoComparacion != null) {
            lblInfoComparacion.setVisible(true);
            lblInfoComparacion.setManaged(true);
            String base = comboCategoria.getValue() != null ? comboCategoria.getValue() : "[Base]";
            switch (modo) {
                case INDIVIDUAL:
                    lblInfoComparacion.setText("Análisis de tendencia individual para " + base);
                    break;
                case COMPARACION_1_VS_1:
                    lblInfoComparacion.setText("Comparación directa entre " + base + " y otra entidad.");
                    break;
                case COMPARACION_1_VS_N:
                    lblInfoComparacion.setText("Comparación de " + base + " frente a un grupo seleccionado.");
                    break;
                case COMPARACION_1_VS_TODOS:
                    lblInfoComparacion.setText("Comparación global de " + base + " frente a TODO el resto.");
                    break;
            }
        }

        filtrarListasComparacion(); // Re-apply filter on mode switch
    }

    @FXML
    private void onModelar() {
        if (!validarInputs())
            return;

        try {
            resultadosCalculados.clear();
            TipoSegmentacion tipo = comboTipoSegmentacion.getValue();
            ModoComparacion modo = comboModoComparacion.getValue();
            int aIni = comboAnioInicial.getValue();
            int aFin = comboAnioFinal.getValue();
            int interval = spinnerIntervalo.getValue();

            // Collect targets
            List<String> targets = new ArrayList<>();
            String base = comboCategoria.getValue();
            targets.add(base);

            if (modo == ModoComparacion.COMPARACION_1_VS_1) {
                targets.add(comboCategoria2.getValue());
            } else if (modo == ModoComparacion.COMPARACION_1_VS_N) {
                targets.addAll(checkComboCategoriaMulti.getCheckModel().getCheckedItems());
            } else if (modo == ModoComparacion.COMPARACION_1_VS_TODOS) {
                List<String> all = obtenerItemsPorTipo(tipo);
                all.remove(base);
                targets.addAll(all);
            }

            // Execute Models
            for (String t : targets) {
                if (t == null)
                    continue;
                ResultadoModeladoEDO res = modelingService.ejecutarModeladoEDO(tipo, t, aIni, aFin, interval);
                if (res != null)
                    resultadosCalculados.add(res);
            }

            // Enable Graph
            if (!resultadosCalculados.isEmpty()) {
                panelResultados.setVisible(true);
                panelResultados.setManaged(true);
                boxMetricas.setVisible(true);
                btnGraficar.setDisable(false);
                chartModelado.getData().clear(); // Reset graph on new model run

                // Generate Text Analysis
                generarAnalisisTexto();
            } else {
                mostrarAlerta("Atención", "No se generaron modelos válidos con los datos actuales.");
            }

        } catch (Exception e) {
            mostrarAlerta("Error Crítico", e.getMessage());
            e.printStackTrace();
        }
    }

    private void generarAnalisisTexto() {
        if (resultadosCalculados.isEmpty())
            return;
        ResultadoModeladoEDO base = resultadosCalculados.get(0);
        StringBuilder sb = new StringBuilder();

        sb.append("Análisis: ").append(base.categoria()).append(" presenta ");
        sb.append(interpretarTendencia(base).toLowerCase()).append(". ");

        if (resultadosCalculados.size() > 1) {
            double avgB = resultadosCalculados.stream().mapToDouble(ResultadoModeladoEDO::parametroB).average()
                    .orElse(0);
            if (base.parametroB() > avgB) {
                sb.append("Su tasa de crecimiento es SUPERIOR al promedio del grupo comparado.");
            } else {
                sb.append("Su tasa de crecimiento es INFERIOR al promedio del grupo comparado.");
            }
        }

        lblAnalisisTexto.setText(sb.toString());
    }

    @FXML
    private void onGraficar() {
        if (resultadosCalculados.isEmpty())
            return;
        chartModelado.getData().clear();
        configurarEjes(resultadosCalculados.get(0));

        int idx = 0;
        for (ResultadoModeladoEDO res : resultadosCalculados) {
            String colorObs = "#1f77b4"; // Blue
            String colorMod = "#d62728"; // Red

            // 1. Observed (Points only)
            XYChart.Series<Number, Number> sObs = new XYChart.Series<>();
            sObs.setName(res.categoria() + " (Datos registrados)");
            for (PuntoTemporal p : res.observados())
                sObs.getData().add(new XYChart.Data<>(p.anio(), p.nacimientos()));
            chartModelado.getData().add(sObs);
            estilizarObservado(sObs, colorObs);

            // 2. Modeled (Smooth Curve)
            XYChart.Series<Number, Number> sMod = new XYChart.Series<>();
            sMod.setName(res.categoria() + " (Modelado)");
            // Use high-res curve if available, else standard
            List<PuntoTemporal> points = (res.modeladosCurve() != null && !res.modeladosCurve().isEmpty())
                    ? res.modeladosCurve()
                    : res.modelados();
            for (PuntoTemporal p : points)
                sMod.getData().add(new XYChart.Data<>(p.anio(), p.nacimientos()));
            chartModelado.getData().add(sMod);
            estilizarModelado(sMod, colorMod);

            idx++;
        }

        corregirColoresLeyenda();
    }

    private void corregirColoresLeyenda() {
        Platform.runLater(() -> {
            javafx.scene.Node legend = chartModelado.lookup(".chart-legend");
            if (legend != null && legend instanceof javafx.scene.layout.Pane) {
                int i = 0;
                for (javafx.scene.Node item : ((javafx.scene.layout.Pane) legend).getChildren()) {
                    if (item instanceof Label) {
                        Label label = (Label) item;
                        javafx.scene.Node symbol = label.getGraphic();
                        if (symbol != null) {
                            // Par = Obs (Azul), Impar = Mod (Rojo)
                            String color = (i % 2 == 0) ? "#1f77b4" : "#d62728";
                            symbol.setStyle("-fx-background-color: " + color + ", white;");
                        }
                        i++;
                    }
                }
            }
        });
    }

    private void estilizarObservado(XYChart.Series<Number, Number> s, String c) {
        Platform.runLater(() -> {
            // Linea visible, punteada para diferenciar del modelo
            if (s.getNode() != null)
                s.getNode().setStyle("-fx-stroke: " + c + "; -fx-stroke-width: 1px; -fx-stroke-dash-array: 5 5;");

            // Puntos visibles y del mismo color
            for (XYChart.Data<Number, Number> d : s.getData()) {
                if (d.getNode() != null) {
                    d.getNode()
                            .setStyle("-fx-background-color: " + c
                                    + ", white; -fx-background-insets: 0, 2; -fx-background-radius: 5px; -fx-padding: 3px;");
                    Tooltip.install(d.getNode(), new Tooltip(s.getName() + ": " + d.getYValue()));
                }
            }
        });
    }

    private void estilizarModelado(XYChart.Series<Number, Number> s, String c) {
        Platform.runLater(() -> {
            if (s.getNode() != null)
                s.getNode().setStyle("-fx-stroke: " + c + "; -fx-stroke-width: 2px;");
            for (XYChart.Data<Number, Number> d : s.getData()) {
                if (d.getNode() != null)
                    d.getNode().setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            }
        });
    }

    private void configurarEjes(ResultadoModeladoEDO r) {
        NumberAxis x = (NumberAxis) chartModelado.getXAxis();
        NumberAxis y = (NumberAxis) chartModelado.getYAxis();

        // Configurar rango exacto seleccionado por el usuario con márgenes
        int inicio = comboAnioInicial.getValue();
        int fin = comboAnioFinal.getValue();

        x.setAutoRanging(false);
        x.setLowerBound(inicio - 1); // Espacio al inicio
        x.setUpperBound(fin + 1); // Espacio al final
        x.setTickUnit(1);

        // Formato X: Entero puro (1999), ocultando los extremos de margen de forma
        // robusta
        x.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                double val = object.doubleValue();
                long year = Math.round(val);

                // Si el año redondeado está fuera del rango [inicio, fin], es margen -> ocultar
                if (year < inicio || year > fin) {
                    return "";
                }
                return String.format(java.util.Locale.US, "%d", year);
            }

            @Override
            public Number fromString(String string) {
                return string.isEmpty() ? 0 : Double.valueOf(string);
            }
        });

        // Formato Y: Entero sin separadores (2500)
        y.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.format(java.util.Locale.US, "%.0f", object.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                return Double.valueOf(string);
            }
        });
    }

    private String interpretarTendencia(ResultadoModeladoEDO r) {
        if (r.parametroB() > 0)
            return "Crecimiento";
        else if (r.parametroB() < 0)
            return "Decrecimiento";
        else
            return "Estancamiento";
    }

    private boolean validarInputs() {
        if (comboCategoria.getValue() == null) {
            mostrarAlerta("Error", "Seleccione Categoría Base");
            return false;
        }
        ModoComparacion m = comboModoComparacion.getValue();
        if (m == ModoComparacion.COMPARACION_1_VS_1 && comboCategoria2.getValue() == null) {
            mostrarAlerta("Error", "Seleccione Segunda Categoría");
            return false;
        }
        if (m == ModoComparacion.COMPARACION_1_VS_N
                && checkComboCategoriaMulti.getCheckModel().getCheckedItems().isEmpty()) {
            mostrarAlerta("Error", "Seleccione al menos una para comparar");
            return false;
        }
        return true;
    }

    private void validarRangoTemporal() {
        if (comboAnioFinal.getValue() < comboAnioInicial.getValue()) {
            comboAnioFinal.setValue(comboAnioInicial.getValue());
            mostrarAlerta("Info", "Año final ajustado.");
        }
    }

    private void mostrarAlerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setContentText(m);
        a.showAndWait();
    }

    @FXML
    private void onLimpiar() {
        resultadosCalculados.clear();
        chartModelado.getData().clear();
        panelResultados.setVisible(false);
        comboCategoria.setValue(null);
        comboCategoria2.setValue(null);
        checkComboCategoriaMulti.getCheckModel().clearChecks();
        lblAnalisisTexto.setText("");
    }

    public List<ResultadoModeladoEDO> getResultadosCalculados() {
        return resultadosCalculados;
    }

    public VBox getPanelResultados() {
        return panelResultados;
    }
}