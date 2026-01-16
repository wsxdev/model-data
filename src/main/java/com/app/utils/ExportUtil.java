package com.app.utils;

import com.app.models.modeling.ResultadoModeladoEDO;
import com.app.models.modeling.PuntoTemporal;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.io.*;
import java.util.List;
import java.util.Locale;

public class ExportUtil {

    public static void exportModelingToCsv(File file, List<ResultadoModeladoEDO> results) throws IOException {
        try (PrintWriter writer = new PrintWriter(
                java.nio.file.Files.newBufferedWriter(file.toPath(), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.println("Categoria,Tipo,ParametroA,ParametroB,R2,MAE,RMSE,Ecuacion");
            for (ResultadoModeladoEDO res : results) {
                writer.printf(Locale.US, "\"%s\",%s,%.4e,%.6f,%.4f,%.2f,%.2f,\"%s\"%n",
                        res.categoria(), res.tipo(), res.parametroA(), res.parametroB(),
                        res.r2(), res.mae(), res.rmse(), res.getEcuacionFormateada());
            }

            writer.println("\n--- DETALLE DE DATOS ---");
            writer.println("Categoria,Anio,Nacimientos_Reales,Nacimientos_Modelados");
            for (ResultadoModeladoEDO res : results) {
                int size = Math.min(res.observados().size(), res.modelados().size());
                for (int i = 0; i < size; i++) {
                    PuntoTemporal obs = res.observados().get(i);
                    PuntoTemporal mod = res.modelados().get(i);
                    writer.printf(Locale.US, "\"%s\",%.0f,%.0f,%.2f%n", res.categoria(), obs.anio(), obs.nacimientos(),
                            mod.nacimientos());
                }
            }
        }
    }

    public static void exportModelingToPdf(File file, List<ResultadoModeladoEDO> results, Node chartNode)
            throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Reporte de Modelado Matemático EDO - ModelData", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Tabla de métricas
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.addCell("Categoría");
        table.addCell("Ecuación");
        table.addCell("Tasa (B)");
        table.addCell("R²");
        table.addCell("MAE");
        table.addCell("RMSE");

        for (ResultadoModeladoEDO res : results) {
            table.addCell(res.categoria());
            table.addCell(res.getEcuacionFormateada());
            table.addCell(String.format("%.6f", res.parametroB()));
            table.addCell(String.format("%.4f", res.r2()));
            table.addCell(String.format("%.2f", res.mae()));
            table.addCell(String.format("%.2f", res.rmse()));
        }
        document.add(table);
        document.add(new Paragraph(" "));

        // Captura de la gráfica (si se proporciona)
        if (chartNode != null) {
            WritableImage image = chartNode.snapshot(new SnapshotParameters(), null);
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", byteOutput);
            Image pdfImage = Image.getInstance(byteOutput.toByteArray());
            pdfImage.scaleToFit(500, 300);
            pdfImage.setAlignment(Element.ALIGN_CENTER);
            document.add(pdfImage);
        }

        document.close();
    }

    public static void openFileLocation(File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                // En Windows, esto abre la carpeta y selecciona el archivo (comillas para rutas
                // con espacios)
                String command = "explorer.exe /select,\"" + file.getAbsolutePath() + "\"";
                Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
