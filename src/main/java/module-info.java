module lab01 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.base;
    requires java.desktop;
    requires javafx.graphics;
    opens lab to javafx.fxml;
    exports lab;
}
