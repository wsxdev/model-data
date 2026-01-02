package com.app.utils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

public class ThemeManagerUtil {

    private static final String PREF_THEME = "app.theme";
    private static final String LIGHT_THEME_CSS = "/com/app/modeldata/css/colors-light.css";
    private static final String DARK_THEME_CSS = "/com/app/modeldata/css/colors-dark.css";
    private static final String MAIN_THEME_CSS = "/com/app/modeldata/css/main.css";

    private static ThemeManagerUtil instance;
    private final Preferences preferences = Preferences.userNodeForPackage(ThemeManagerUtil.class);
    private final List<Stage> registeredStages = new ArrayList<>();
    private ThemeMode themeMode;

    // CONSTRUCTOR
    private ThemeManagerUtil() {
        String mode = preferences.get(PREF_THEME, ThemeMode.AUTO.name());
        try {
            themeMode = ThemeMode.valueOf(mode);
        } catch (Exception e) { themeMode = ThemeMode.AUTO; }
    }

    // GETTERS - SETTERS
    public ThemeMode getThemeMode() { return themeMode; }
    public void setThemeMode(ThemeMode newMode) {
        this.themeMode = newMode;
        preferences.put(PREF_THEME, newMode.name());
        applyToAllStages();
    }

    // MÉTODO PARA APLICAR EL TEMA A UNA ESCENA
    public static synchronized ThemeManagerUtil getInstance() {
        if (instance == null) instance = new ThemeManagerUtil();
        return instance;
    }

    // MÉTODO PARA REGISTRAR UN STAGE PARA APLICARLE EL TEMA
    public void registerStage(Stage stage) {
        if (!registeredStages.contains(stage)) registeredStages.add(stage);
        applyToStage(stage);
    }

    // MÉTODO PARA APLICAR EL TEMA A UN STAGE
    public void applyToStage(Stage stage) {
        if (stage ==  null) return;
        Scene scene = stage.getScene();
        if (scene != null) applyToScene(scene);
    }

    // MÉTODO PARA APLICAR EL TEMA A UNA ESCENA
    public void applyToScene(Scene scene) {
        if  (scene == null) return;
        ThemeMode effectiveMode = resolveEffectiveMode();
        String colorCss;
        if (effectiveMode == ThemeMode.DARK) {
            colorCss = DARK_THEME_CSS;
        } else {
            colorCss = LIGHT_THEME_CSS;
        }
        // APLICAR EL TEMA A LA ESCENA
        Platform.runLater(() -> {
            try {
                scene.getStylesheets().clear();
                String mainCss = getClass().getResource(MAIN_THEME_CSS) != null ? Objects.requireNonNull(getClass().getResource(MAIN_THEME_CSS)).toExternalForm() : null;
                String colors = getClass().getResource(colorCss) != null ? Objects.requireNonNull(getClass().getResource(colorCss)).toExternalForm() : null;
                if (colors != null) scene.getStylesheets().add(colors);
                if (mainCss != null) scene.getStylesheets().add(mainCss);
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    // MÉTODO PARA APLICAR EL TEMA A TODOS LOS STAGES REGISTRADOS
    private void applyToAllStages() {
        for (Stage stage : new ArrayList<>(registeredStages)) applyToStage(stage);
    }

    // MÉTODO PARA RESOLVER EL MODO EFECTIVO DEL TEMA
    private ThemeMode resolveEffectiveMode() {
        if (themeMode == ThemeMode.AUTO) {
            return isSystemDark() ? ThemeMode.DARK : ThemeMode.LIGHT;
        }
        return themeMode;
    }

    // MÉTODO PARA DETECTAR SI EL SISTEMA ESTÁ EN MODO OSCURO
    private boolean isSystemDark() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        // LÓGICA PARA DETECTAR EL TEMA OSCURO EN DIFERENTES SISTEMAS OPERATIVOS
        try {
            String gtkTheme = System.getenv("GTK_THEME");
            if (gtkTheme != null && gtkTheme.toLowerCase().contains("dark")) return true;

            String desktopTheme = System.getenv("XDG_CURRENT_DESKTOP");
            // DETECCIÓN PARA GNOME
            if (desktopTheme != null && desktopTheme.toLowerCase().contains("gnome")) {
                // INTENTAR CON GSETTINGS (GNOME)
                ProcessBuilder processBuilder = new ProcessBuilder("gsettings", "get", "org.gnome.desktop.interface", "color-scheme");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                boolean success = process.waitFor(1500, TimeUnit.MILLISECONDS);
                // VERIFICAR SALIDA DE GSETTINGS
                if (success) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line = reader.readLine();
                        if (line != null && line.toLowerCase().contains("prefer-dark")) return true;
                    }
                } else { process.destroy(); }

                // FALLBACK: VERIFICA GTK THEME
                processBuilder = new ProcessBuilder("gsettings", "get", "org.gnome.desktop.interface", "gtk-theme");
                processBuilder.redirectErrorStream(true);
                process = processBuilder.start();
                success = process.waitFor(1500, TimeUnit.MILLISECONDS);
                // VERIFICAR SALIDA DE GSETTINGS
                if (success) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line = reader.readLine();
                        if (line != null && line.toLowerCase().contains("dark")) return true;
                    }
                } else { process.destroy(); }
            }
            // DETECCIÓN PARA MACOS Y WINDOWS
            if (osName.contains("mac") || osName.contains("darwin")) {
                ProcessBuilder processBuilder = new ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                boolean success = process.waitFor(1500, TimeUnit.MILLISECONDS);

                if (success) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line = reader.readLine();
                        return line != null && line.toLowerCase().contains("dark");
                    }
                } else { process.destroy(); }
            } else if (osName.contains("win")) { // WINDOWS
                // VERIFICAR EL TEMA DE LA APLICACIÓN EN WINDOWS
                ProcessBuilder processBuilder = new ProcessBuilder("reg", "query", "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize", "/v", "AppsUseLightTheme");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                boolean success = process.waitFor(1500, TimeUnit.MILLISECONDS);
                // VERIFICAR SALIDA DEL REGISTRO
                if (success) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        // BUSCAR LA LÍNEA QUE CONTIENE LA CONFIGURACIÓN DEL TEMA
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("AppsUseLightTheme")) {
                                if (line.trim().endsWith("0")) return true; // TEMA DARK
                                else return false;
                            }
                        }
                    }
                } else { process.destroy(); }
            } else {
                String theme = System.getenv("GTK_THEME");
                // VERIFICAR SI EL TEMA ESTÁ EN MODO OSCURO
                if (theme != null && theme.toLowerCase().contains("dark")) return true;
                String kdeColor = System.getenv("KDE_FULL_SESSION");
                if (kdeColor != null && kdeColor.equalsIgnoreCase("true")) {
                    String qtStyle = System.getenv("QT_QPA_PLATFORMTHEME");
                    if (qtStyle != null && qtStyle.toLowerCase().contains("gtk")) {
                        return theme != null && theme.toLowerCase().contains("dark");

                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }
}
