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

    opens com.app.modeldata to javafx.fxml;
    opens com.app.controllers.modeldata to javafx.fxml;
    opens com.app.controllers.login to javafx.fxml;
    exports com.app.modeldata;
}