package com.app.controllers.panels.sidebar;

import com.app.models.modeling.*;
import com.app.models.services.ModelingService;
import com.app.models.services.BirthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controlador para el módulo de Modelado Matemático EDO.
 * Permite seleccionar datos, ejecutar modelado y visualizar resultados con
 * gráfica integrada.
 */
public class ModeladoController {

    // Enums para modos de comparación
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

    // Controles de segmentación
    @FXML
    private ComboBox<TipoSegmentacion> comboTipoSegmentacion;
    @FXML
    private ComboBox<ModoComparacion> comboModoComparacion;

    @FXML
    private ComboBox<String> comboCategoria;
    @FXML
    private HBox boxCategoria2;
    @FXML
    private ComboBox<String> comboCategoria2;

    @FXML
    private HBox boxCategoriaMulti;
    @FXML
    private CheckComboBox<String> checkComboCategoriaMulti;

    // Controles de rango temporal
    @FXML
    private ComboBox<Integer> comboAnioInicial;
    @FXML
    private ComboBox<Integer> comboAnioFinal;
    @FXML
    private Spinner<Integer> spinnerIntervalo;

    // Botones
    @FXML
    private Button btnModelar;
    @FXML
    private Button btnGraficar;
    @FXML
    private Button btnLimpiar;

    // Panel de resultados
    @FXML
    private VBox panelResultados;
    @FXML
    private VBox boxMetricas;

    // Gráfica
    @FXML
    private LineChart<Number, Number> chartModelado;

    // Métricas
    @FXML
    private Label lblEcuacion;
    @FXML
    private Label lblParametroA;
    @FXML
    private Label lblParametroB;
    @FXML
    private Label lblTipoCrecimiento;
    @FXML
    private Label lblR2;
    @FXML
    private Label lblMAE;
    @FXML
    private Label lblRMSE;

    private ModelingService modelingService;
    private BirthService birthService;

    // Almacenamiento de resultados
    private List<ResultadoModeladoEDO> resultadosCalculados = new ArrayList<>();

    @FXML
    public void initialize() {
        modelingService = new ModelingService();
        birthService = new BirthService();

        configurarControles();
        configurarListeners();
    }

    /**
     * Configura los valores iniciales de los controles.
     */
    private void configurarControles() {
        // Cargar tipos de segmentación
        comboTipoSegmentacion.getItems().addAll(
                TipoSegmentacion.PROVINCIA,
                TipoSegmentacion.INSTRUCCION);

        comboTipoSegmentacion.setConverter(new StringConverter<TipoSegmentacion>() {
            @Override
            public String toString(TipoSegmentacion tipo) {
                return tipo != null ? tipo.getDisplayName() : "";
            }

            @Override
            public TipoSegmentacion fromString(String string) {
                return null;
            }
        });

        // Configurar Modos de Comparación
        comboModoComparacion.getItems().addAll(ModoComparacion.values());

        // Converter para i18n de modos
        comboModoComparacion.setConverter(new StringConverter<ModoComparacion>() {
            @Override
            public String toString(ModoComparacion modo) {
                if (modo == null)
                    return "";
                try {
                    return java.util.ResourceBundle.getBundle("i18n/messages").getString(modo.getKey());
                } catch (Exception e) {
                    return modo.name();
                }
            }

            @Override
            public ModoComparacion fromString(String string) {
                return null;
            }
        });

        comboModoComparacion.setValue(ModoComparacion.INDIVIDUAL);

        // Cargar años (1990-2024)
        List<Integer> years = IntStream.rangeClosed(1990, 2024).boxed().collect(Collectors.toList());
        comboAnioInicial.getItems().addAll(years);
        comboAnioFinal.getItems().addAll(years);
        comboAnioInicial.setValue(2000);
        comboAnioFinal.setValue(2020);

        // Intervalo
        spinnerIntervalo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    /**
     * Configura los listeners para eventos de los controles.
     */
    private void configurarListeners() {
        comboTipoSegmentacion.setOnAction(e -> {
            cargarCategorias();
            actualizarEstadoUI();
        });

        comboModoComparacion.setOnAction(e -> atualizarEstadoUI());

        comboAnioFinal.setOnAction(e -> validarRangoTemporal());
    }

    /**
     * Actualiza la visibilidad y estado de los controles según selección.
     */
    private void actualizarEstadoUI() {
        TipoSegmentacion tipo = comboTipoSegmentacion.getValue();
        ModoComparacion modo = comboModoComparacion.getValue();

        if (tipo == null || modo == null)
            return;

        // Regla: Instrucción solo permite Individual o 1 vs 1 (max 2)
        if (tipo == TipoSegmentacion.INSTRUCCION) {
            if (modo == ModoComparacion.COMPARACION_1_VS_N || modo == ModoComparacion.COMPARACION_1_VS_TODOS) {
                // Reset a individual si selecciona uno inválido
                comboModoComparacion.setValue(ModoComparacion.INDIVIDUAL);
                mostrarAlerta("Información",
                        "Para Nivel de Instrucción, solo se permite comparación entre dos niveles.");
                return;
            }
        }

        // Visibilidad de selectores secundarios
        boolean showCat2 = (modo == ModoComparacion.COMPARACION_1_VS_1);
        boolean showMulti = (modo == ModoComparacion.COMPARACION_1_VS_N);

        boxCategoria2.setVisible(showCat2);
        boxCategoria2.setManaged(showCat2);

        boxCategoriaMulti.setVisible(showMulti);
        boxCategoriaMulti.setManaged(showMulti);
    }

    // Typo fix in listener
    private void atualizarEstadoUI() {
        actualizarEstadoUI();
    }

    private void cargarCategorias() {
        comboCategoria.getItems().clear();
        comboCategoria2.getItems().clear();
        checkComboCategoriaMulti.getItems().clear();

        TipoSegmentacion tipo = comboTipoSegmentacion.getValue();
        if (tipo == null)
            return;

        try {
            List<String> items = new ArrayList<>();
            if (tipo == TipoSegmentacion.PROVINCIA) {
                items = birthService.getAllProvinces().stream().map(p -> p.getNameProvince())
                        .collect(Collectors.toList());
            } else if (tipo == TipoSegmentacion.INSTRUCCION) {
                items = birthService.getAllInstructions().stream().map(i -> i.getNameInstruction())
                        .collect(Collectors.toList());
            }

            comboCategoria.getItems().addAll(items);
            comboCategoria2.getItems().addAll(items);
            checkComboCategoriaMulti.getItems().addAll(items);

        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al cargar categorías: " + ex.getMessage());
        }
    }

    private void validarRangoTemporal() {
        Integer inicio = comboAnioInicial.getValue();
        Integer fin = comboAnioFinal.getValue();
        if (inicio != null && fin != null && fin < inicio) {
            mostrarAlerta("Error", "El año final debe ser mayor o igual al año inicial");
            comboAnioFinal.setValue(inicio);
        }
    }

    @FXML
    private void onModelar() {
        if (!validarInputs())
            return;

        try {
            resultadosCalculados.clear();
            TipoSegmentacion tipo = comboTipoSegmentacion.getValue();
            ModoComparacion modo = comboModoComparacion.getValue();
            int anioInicio = comboAnioInicial.getValue();
            int anioFin = comboAnioFinal.getValue();
            int intervalo = spinnerIntervalo.getValue();

            // Lista de categorías a procesar
            List<String> categoriasAProcesar = new ArrayList<>();

            // 1. Categoría Base (siempre)
            String catBase = comboCategoria.getValue();
            categoriasAProcesar.add(catBase);

            // 2. Categorías adicionales según modo
            switch (modo) {
                case COMPARACION_1_VS_1:
                    String cat2 = comboCategoria2.getValue();
                    if (cat2 != null && !cat2.equals(catBase)) {
                        categoriasAProcesar.add(cat2);
                    }
                    break;
                case COMPARACION_1_VS_N:
                    List<String> selected = checkComboCategoriaMulti.getCheckModel().getCheckedItems();
                    for (String s : selected) {
                        if (!s.equals(catBase) && !categoriasAProcesar.contains(s)) {
                            categoriasAProcesar.add(s);
                        }
                    }
                    break;
                case COMPARACION_1_VS_TODOS:
                    // Agregar todas las disponibles excepto la base (ya agregada)
                    for (String item : comboCategoria.getItems()) {
                        if (!item.equals(catBase)) {
                            categoriasAProcesar.add(item);
                        }
                    }
                    break;
                default:
                    break;
            }

            // Ejecutar modelos
            for (String cat : categoriasAProcesar) {
                ResultadoModeladoEDO resultado = modelingService.ejecutarModeladoEDO(tipo, cat, anioInicio, anioFin,
                        intervalo);
                if (resultado != null) {
                    resultadosCalculados.add(resultado);
                }
            }

            // Habilitar botón graficar
            btnGraficar.setDisable(resultadosCalculados.isEmpty());

            // Mostrar métricas del PRIMER elemento (Base) para feedback inmediato
            if (!resultadosCalculados.isEmpty()) {
                mostrarMetricas(resultadosCalculados.get(0));
                panelResultados.setVisible(true);
                panelResultados.setManaged(true);
                // Ocultar métricas si es comparación masiva para no confundir, o dejarlas solo
                // para el base
                boxMetricas.setVisible(true); // Siempre mostrar base
                // Limpiar gráfica previa para indicar que hay nuevos datos listos
                chartModelado.getData().clear();
            }

            mostrarAlerta("Éxito", "Modelado completado. Se generaron " + resultadosCalculados.size()
                    + " modelos. Presione 'Graficar' para visualizar.");

        } catch (Exception e) {
            mostrarAlerta("Error", "Error durante el modelado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onGraficar() {
        if (resultadosCalculados.isEmpty())
            return;

        chartModelado.getData().clear();

        // Configurar Eje X basado en el primer resultado (asumiendo mismo rango para
        // todos)
        configurarEjeXDinamico(resultadosCalculados.get(0));

        // Paleta de colores para múltiples series
        String[] colors = { "blue", "green", "orange", "purple", "brown", "magenta", "teal", "navy" };

        int colorIndex = 0;

        for (ResultadoModeladoEDO resultado : resultadosCalculados) {
            String baseColor = (colorIndex == 0) ? "blue"
                    : (resultado == resultadosCalculados.get(0) ? "blue" : colors[colorIndex % colors.length]);
            if (colorIndex == 1 && resultadosCalculados.size() == 2)
                baseColor = "red"; // Forzar rojo para 2da en 1v1

            // Serie Reales (Puntos)
            XYChart.Series<Number, Number> serieReal = new XYChart.Series<>();
            serieReal.setName(resultado.categoria() + " (Obs)");
            for (PuntoTemporal p : resultado.observados()) {
                serieReal.getData().add(new XYChart.Data<>(p.anio(), p.nacimientos()));
            }

            // Serie Modelo (Línea)
            XYChart.Series<Number, Number> serieModelo = new XYChart.Series<>();
            serieModelo.setName(resultado.categoria() + " (Mod)");
            for (PuntoTemporal p : resultado.modelados()) {
                serieModelo.getData().add(new XYChart.Data<>(p.anio(), p.nacimientos()));
            }

            chartModelado.getData().addAll(serieReal, serieModelo);

            // Estilizar
            estilizarSerie(serieReal, serieModelo, baseColor);

            colorIndex++;
        }
    }

    private void estilizarSerie(XYChart.Series<Number, Number> real, XYChart.Series<Number, Number> model,
            String color) {
        Platform.runLater(() -> {
            // Estilo Real: Puntos visibles, sin línea
            if (real.getNode() != null) {
                real.getNode().setStyle("-fx-stroke: transparent;");
            }
            for (XYChart.Data<Number, Number> data : real.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle(
                            "-fx-background-color: " + color + "; -fx-background-radius: 5px; -fx-padding: 3px;");
                    Tooltip.install(data.getNode(),
                            new Tooltip(real.getName() + "\n" + data.getXValue() + ": " + data.getYValue()));
                }
            }

            // Estilo Modelo: Línea visible, sin puntos (o puntos pequeños)
            if (model.getNode() != null) {
                model.getNode()
                        .setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 2px; -fx-stroke-type: centered;");
            }
            for (XYChart.Data<Number, Number> data : model.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-background-color: transparent, transparent;"); // Ocultar puntos del
                                                                                                // modelo
                    Tooltip.install(data.getNode(),
                            new Tooltip(model.getName() + "\n" + data.getXValue() + ": " + data.getYValue()));
                }
            }
        });
    }

    private boolean validarInputs() {
        if (comboTipoSegmentacion.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un tipo de segmentación");
            return false;
        }
        if (comboCategoria.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione una categoría base");
            return false;
        }
        // Validaciones extra para modos
        if (comboModoComparacion.getValue() == ModoComparacion.COMPARACION_1_VS_1
                && comboCategoria2.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione la segunda categoría para comparar.");
            return false;
        }
        if (comboAnioInicial.getValue() == null || comboAnioFinal.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el rango temporal");
            return false;
        }
        return true;
    }

    private void configurarEjeXDinamico(ResultadoModeladoEDO resultado) {
        if (resultado.observados().isEmpty())
            return;
        int minAnio = resultado.observados().stream().mapToInt(PuntoTemporal::anio).min().orElse(1990);
        int maxAnio = resultado.observados().stream().mapToInt(PuntoTemporal::anio).max().orElse(2024);
        NumberAxis xAxis = (NumberAxis) chartModelado.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(minAnio - 0.5);
        xAxis.setUpperBound(maxAnio + 0.5);
        xAxis.setTickUnit(1);
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                double val = object.doubleValue();
                return (Math.abs(val - Math.round(val)) < 0.1) ? String.format("%d", (int) Math.round(val)) : "";
            }
        });
    }

    private void mostrarMetricas(ResultadoModeladoEDO resultado) {
        lblEcuacion.setText(resultado.getEcuacionFormateada());
        lblParametroA.setText(String.format("%.6e", resultado.parametroA()));
        lblParametroB.setText(String.format("%.6f", resultado.parametroB()));
        String tipoCrecimiento = resultado.parametroB() > 0 ? "Crecimiento" : "Decrecimiento";
        lblTipoCrecimiento.setText(tipoCrecimiento);
        lblTipoCrecimiento.setStyle(resultado.parametroB() > 0
                ? "-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3;"
                : "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3;");
        lblR2.setText(String.format("%.4f", resultado.r2()));
        lblMAE.setText(String.format("%.2f", resultado.mae()));
        lblRMSE.setText(String.format("%.2f", resultado.rmse()));
    }

    @FXML
    private void onLimpiar() {
        comboTipoSegmentacion.setValue(null);
        comboCategoria.getItems().clear();
        comboCategoria2.getItems().clear();
        checkComboCategoriaMulti.getItems().clear();
        resultadosCalculados.clear();
        panelResultados.setVisible(false);
        panelResultados.setManaged(false);
        chartModelado.getData().clear();
        btnGraficar.setDisable(true);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}