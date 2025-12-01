module com.app.modeldata {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    // PERMITIR EL ACCESO A LOS PAQUETES QUE CONTIENEN LOS CONTROLADORES FXML
    opens com.app.modeldata to javafx.fxml;
    opens com.app.controllers.modeldata to javafx.fxml;
    opens com.app.controllers.panels to javafx.fxml;
    opens com.app.controllers.login to javafx.fxml;
    exports com.app.modeldata;
}