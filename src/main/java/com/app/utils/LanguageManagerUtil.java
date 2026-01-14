package com.app.utils;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.*;
import java.util.prefs.Preferences;

public class LanguageManagerUtil {

    private static final String PREF_LOCALE = "app.locale";
    private static final List<Locale> SUPPORTED = List.of(
            new Locale("en"),
            new Locale("es"),
            new Locale("fr"),
            new Locale("de"),
            new Locale("pt"),
            new Locale("ja"),
            new Locale("zh"),
            new Locale("ar")
    );

    private static LanguageManagerUtil instance;
    private final Preferences preferences = Preferences.userNodeForPackage(LanguageManagerUtil.class);
    private Locale locale;
    private final List<Runnable> listeners = new ArrayList<>();
    private final List<Stage> registeredStages = new ArrayList<>();
    private final Map<Stage, Runnable> stageRefreshers = new HashMap<>();

    private LanguageManagerUtil() {
        String defaultLanguage = preferences.get(PREF_LOCALE, Locale.getDefault().getLanguage());
        Locale locale = Locale.forLanguageTag(defaultLanguage);
        if (!SUPPORTED.contains(locale)) {
            // FALLBACK: BÚSQUEDA POR CÓDIGO DE IDIOMA
            for (Locale supported : SUPPORTED) if (supported.getLanguage().equalsIgnoreCase(defaultLanguage)) { locale = supported; break; }
        }
        if (!SUPPORTED.contains(locale)) locale = Locale.ENGLISH;
        this.locale = locale;
    }

    // MÉTODO PARA OBTENER LA INSTANCIA SINGLETON
    public static synchronized LanguageManagerUtil getInstance() {
        if (instance == null) instance = new LanguageManagerUtil();
        return instance;
    }

    // GETTERS - SETTERS
    public Locale getLocale() { return locale; }

    public void setLocale(Locale newLocale) {
        if (newLocale == null) return;
        this.locale = newLocale;
        preferences.put(PREF_LOCALE, newLocale.getLanguage());
        // NOTIFICAR A LOS LISTENERS EN JavaFX APP THREAD
        Platform.runLater(() -> {
            for (Runnable runnable : new ArrayList<>(listeners)) {
                try { runnable.run(); } catch (Exception ignored) {}
            }
        });
    }

    // MÉTODO PARA OBTENER EL RESOURCE BUNDLE SEGÚN EL IDIOMA ACTUAL
    public ResourceBundle getBundle() {
        try { return ResourceBundle.getBundle("i18n.messages", locale); }
        catch (Exception e) { return ResourceBundle.getBundle("i18n.messages", Locale.ENGLISH); }
    }
    // MÉTODO PARA OBTENER LA LISTA DE LOCALES SOPORTADOS
    public List<Locale> getSupportedLocales() { return Collections.unmodifiableList(SUPPORTED); }
    // MÉTODOS PARA GESTIONAR LISTENERS DE CAMBIO DE IDIOMA
    public void addLocaleChangeListener(Runnable runnable) { if (runnable != null) listeners.add(runnable); }
    public void removeLocaleChangeListener(Runnable runnable) { listeners.remove(runnable); }

    // REGISTRAR STAGE Y UNA ACCIÓN DE RECARGA (refresh) QUE SE EJECUTARÁ AL CAMBIAR EL IDIOMA
    public void registerStage(Stage stage, Runnable refreshAction) {
        if (stage == null) return;
        if (!registeredStages.contains(stage)) registeredStages.add(stage);
        if (refreshAction != null) {
            stageRefreshers.put(stage, refreshAction);
            addLocaleChangeListener(refreshAction);
        }
        // LIMPIAR REFERENCIAS AL CERRAR EL STAGE
        try {
            stage.setOnHidden(l -> {
                registeredStages.remove(stage);
                Runnable runnable = stageRefreshers.remove(stage);
                if (runnable != null) removeLocaleChangeListener(runnable);
            });
        } catch (Exception ignored) {}
    }

    public void registerStage(Stage stage) { registerStage(stage, null); }

    // APLICAR RECARGA A TODOS LOS STAGES REGISTRADOS
    public void applyToAllRegisteredStages() {
        Platform.runLater(() -> {
            for (Stage stage : new ArrayList<>(registeredStages)) {
                Runnable runnable = stageRefreshers.get(stage);
                if (runnable != null) {
                    try { runnable.run(); } catch (Exception ignored) {}
                }
            }
        });
    }
}
