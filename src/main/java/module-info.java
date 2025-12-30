module lab01 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.base;
    requires java.desktop;
    requires javafx.graphics;
    requires java.logging;
    opens lab to javafx.fxml;
    exports lab;
}
