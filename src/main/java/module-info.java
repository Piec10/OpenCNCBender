module com.opencncbender {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.math3;
    requires java.desktop;
    requires java.scripting;

    opens com.opencncbender.controller to javafx.fxml;
    exports com.opencncbender;
}