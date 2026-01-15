package com.app.controllers.panels.sidebar;

import com.app.models.modeling.*;
import com.app.models.services.ModelingService;
import com.app.models.services.BirthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controlador para el módulo de Modelado Matemático EDO.
 * Permite seleccionar datos, ejecutar modelado y visualizar resultados con
 * gráfica integrada.
 */
public class ModeladoController {

    // Controles de segmentación
    @FXML
    private ComboBox<TipoSegmentacion> comboTipoSegmentacion;
    @FXML
    private ComboBox<String> comboCategoria;

    // Controles de rango temporal
    @FXML
    private ComboBox<Integer> comboAnioInicial;
    @FXML
    private ComboBox<Integer> comboAnioFinal;
    @FXML
    private Spinner<Integer> spinnerIntervalo;

    // Botones
    @FXML
    private Button btnAplicarModelado;
    @FXML
    private Button btnLimpiar;

    // Panel de resultados
    @FXML
    private VBox panelResultados;

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
    private ResultadoModeladoEDO resultadoActual;

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
        // Cargar tipos de segmentación (solo Provincia e Instrucción)
        comboTipoSegmentacion.getItems().addAll(
                TipoSegmentacion.PROVINCIA,
                TipoSegmentacion.INSTRUCCION);

        // Usar StringConverter para mostrar el displayName
        comboTipoSegmentacion.setConverter(new javafx.util.StringConverter<TipoSegmentacion>() {
            @Override
            public String toString(TipoSegmentacion tipo) {
                return tipo != null ? tipo.getDisplayName() : "";
            }

            @Override
            public TipoSegmentacion fromString(String string) {
                return null; // No usado
            }
        });

        // Cargar años disponibles (1990-2024)
        List<Integer> years = IntStream.rangeClosed(1990, 2024)
                .boxed()
                .collect(Collectors.toList());
        comboAnioInicial.getItems().addAll(years);
        comboAnioFinal.getItems().addAll(years);

        // Valores por defecto
        comboAnioInicial.setValue(2000);
        comboAnioFinal.setValue(2020);

        // Configurar spinner de intervalo
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        spinnerIntervalo.setValueFactory(valueFactory);
    }

    /**
     * Configura los listeners para eventos de los controles.
     */
    private void configurarListeners() {
        // Actualizar categorías cuando cambia el tipo
        comboTipoSegmentacion.setOnAction(e -> cargarCategorias());

        // Validar que año final >= año inicial
        comboAnioFinal.setOnAction(e -> validarRangoTemporal());
    }

    /**
     * Carga las categorías disponibles según el tipo de segmentación seleccionado.
     */
    private void cargarCategorias() {
        comboCategoria.getItems().clear();
        TipoSegmentacion tipo = comboTipoSegmentacion.getValue();

        if (tipo == null)
            return;

        try {
            if (tipo == TipoSegmentacion.PROVINCIA) {
                birthService.getAllProvinces().forEach(p -> comboCategoria.getItems().add(p.getNameProvince()));
            } else if (tipo == TipoSegmentacion.INSTRUCCION) {
                birthService.getAllInstructions().forEach(i -> comboCategoria.getItems().add(i.getNameInstruction()));
            }
        } catch (Exception ex) {
            mostrarAlerta("Error", "Error al cargar categorías: " + ex.getMessage());
        }
    }

    /**
     * Valida que el año final sea mayor o igual al año inicial.
     */
    private void validarRangoTemporal() {
        Integer inicio = comboAnioInicial.getValue();
        Integer fin = comboAnioFinal.getValue();

        if (inicio != null && fin != null && fin < inicio) {
            mostrarAlerta("Error", "El año final debe ser mayor o igual al año inicial");
            comboAnioFinal.setValue(inicio);
        }
    }

    /**
     * Ejecuta el modelado EDO al presionar el botón "Aplicar Modelado EDO".
     */
    @FXML
    private void onAplicarModelado() {
        // Validar inputs
        if (!validarInputs())
            return;

        try {
            // Obtener parámetros
            TipoSegmentacion tipo = comboTipoSegmentacion.getValue();
            String categoria = comboCategoria.getValue();
            int anioInicio = comboAnioInicial.getValue();
            int anioFin = comboAnioFinal.getValue();
            int intervalo = spinnerIntervalo.getValue();

            // Ejecutar modelado
            resultadoActual = modelingService.ejecutarModeladoEDO(
                    tipo, categoria, anioInicio, anioFin, intervalo);

            // Mostrar resultados
            mostrarGrafica(resultadoActual);
            mostrarMetricas(resultadoActual);

            // Hacer visible el panel
            panelResultados.setVisible(true);
            panelResultados.setManaged(true);

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al ejecutar el modelado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida que todos los inputs necesarios estén seleccionados.
     */
    private boolean validarInputs() {
        if (comboTipoSegmentacion.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione un tipo de segmentación");
            return false;
        }
        if (comboCategoria.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione una categoría");
            return false;
        }
        if (comboAnioInicial.getValue() == null || comboAnioFinal.getValue() == null) {
            mostrarAlerta("Validación", "Seleccione el rango temporal");
            return false;
        }
        return true;
    }

    /**
     * Muestra la gráfica con datos reales y modelados.
     */
    private void mostrarGrafica(ResultadoModeladoEDO resultado) {
        chartModelado.getData().clear();

        // Configurar dinámicamente el eje X según el rango y el intervalo
        configurarEjeXDinamico(resultado);

        // Serie 1: Datos reales (puntos azules)
        XYChart.Series<Number, Number> serieReal = new XYChart.Series<>();
        serieReal.setName("Datos Reales");
        for (PuntoTemporal punto : resultado.observados()) {
            serieReal.getData().add(new XYChart.Data<>(punto.anio(), punto.nacimientos()));
        }

        // Serie 2: Datos modelados (línea roja)
        XYChart.Series<Number, Number> serieModelada = new XYChart.Series<>();
        serieModelada.setName("Modelo EDO");
        for (PuntoTemporal punto : resultado.modelados()) {
            serieModelada.getData().add(new XYChart.Data<>(punto.anio(), punto.nacimientos()));
        }

        chartModelado.getData().addAll(serieReal, serieModelada);

        // Aplicar estilos CSS para diferenciar las series
        aplicarEstilosGrafica();
    }

    /**
     * Aplica estilos CSS a las series de la gráfica.
     * Serie 0 (real): puntos azules
     * Serie 1 (modelada): línea roja continua
     */
    private void aplicarEstilosGrafica() {
        // Ejecutar después de que se renderice la gráfica para asegurar que los nodos
        // existan
        javafx.application.Platform.runLater(() -> {
            if (chartModelado.getData().size() >= 2) {
                // Serie 0: Datos Reales (Azul)
                XYChart.Series<Number, Number> seriesReal = chartModelado.getData().get(0);
                if (seriesReal.getNode() != null) {
                    seriesReal.getNode().setStyle("-fx-stroke: blue;");
                }
                for (XYChart.Data<Number, Number> data : seriesReal.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-background-color: blue, white;");
                        // Instalar Tooltip
                        Tooltip tooltip = new Tooltip(
                                String.format("Año: %d\nNacimientos: %d",
                                        data.getXValue().intValue(),
                                        data.getYValue().intValue()));
                        Tooltip.install(data.getNode(), tooltip);
                    }
                }

                // Serie 1: Datos Modelados (Rojo)
                XYChart.Series<Number, Number> seriesModel = chartModelado.getData().get(1);
                if (seriesModel.getNode() != null) {
                    seriesModel.getNode().setStyle("-fx-stroke: red;");
                }
                for (XYChart.Data<Number, Number> data : seriesModel.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-background-color: red, white;");
                        // Instalar Tooltip
                        Tooltip tooltip = new Tooltip(
                                String.format("Año: %d\nModelado: %d",
                                        data.getXValue().intValue(),
                                        data.getYValue().intValue()));
                        Tooltip.install(data.getNode(), tooltip);
                    }
                }
            }
        });
    }

    /**
     * Configura dinámicamente el eje X (años) según el rango y el intervalo
     * seleccionado.
     */
    private void configurarEjeXDinamico(ResultadoModeladoEDO resultado) {
        if (resultado.observados().isEmpty()) {
            return;
        }

        // Obtener el rango de años de los datos observados
        int minAnio = resultado.observados().stream()
                .mapToInt(PuntoTemporal::anio)
                .min()
                .orElse(1990);
        int maxAnio = resultado.observados().stream()
                .mapToInt(PuntoTemporal::anio)
                .max()
                .orElse(2024);

        // Configurar padding fraccional (0.5)
        double lowerBound = minAnio - 0.5;
        double upperBound = maxAnio + 0.5;

        // Forzar intervalo de 1 año para mostrar "año a año"
        double tickUnit = 1;

        // Obtener el NumberAxis del eje X
        NumberAxis xAxis = (NumberAxis) chartModelado.getXAxis();

        // Configurar el eje X
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(lowerBound);
        xAxis.setUpperBound(upperBound);
        xAxis.setTickUnit(tickUnit);
        xAxis.setMinorTickVisible(false);

        // Forzar que solo muestre números enteros (sin decimales)
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                // Solo mostrar si el valor está muy cerca de un entero
                double val = object.doubleValue();
                if (Math.abs(val - Math.round(val)) < 0.1) {
                    return String.format("%d", (int) Math.round(val));
                }
                return "";
            }
        });

    }

    /**
     * Muestra las métricas del modelo en los labels correspondientes.
     */
    private void mostrarMetricas(ResultadoModeladoEDO resultado) {
        lblEcuacion.setText(resultado.getEcuacionFormateada());
        lblParametroA.setText(String.format("%.6e", resultado.parametroA()));
        lblParametroB.setText(String.format("%.6f", resultado.parametroB()));

        String tipoCrecimiento = resultado.parametroB() > 0 ? "Crecimiento" : "Decrecimiento";
        lblTipoCrecimiento.setText(tipoCrecimiento);

        // Aplicar estilo según el tipo
        if (resultado.parametroB() > 0) {
            lblTipoCrecimiento.setStyle(
                    "-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3;");
        } else {
            lblTipoCrecimiento.setStyle(
                    "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3;");
        }

        lblR2.setText(String.format("%.4f", resultado.r2()));
        lblMAE.setText(String.format("%.2f", resultado.mae()));
        lblRMSE.setText(String.format("%.2f", resultado.rmse()));
    }

    /**
     * Limpia todos los controles y oculta el panel de resultados.
     */
    @FXML
    private void onLimpiar() {
        // Limpiar selecciones
        comboTipoSegmentacion.setValue(null);
        comboCategoria.getItems().clear();
        comboAnioInicial.setValue(2000);
        comboAnioFinal.setValue(2020);
        spinnerIntervalo.getValueFactory().setValue(1);

        // Ocultar resultados
        panelResultados.setVisible(false);
        panelResultados.setManaged(false);

        // Limpiar gráfica
        chartModelado.getData().clear();

        // Limpiar resultado actual
        resultadoActual = null;
    }

    /**
     * Muestra un diálogo de alerta con el mensaje especificado.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}