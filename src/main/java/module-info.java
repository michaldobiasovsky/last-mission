module net.dobiasovsky.michal.stargate {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.logging;

    opens net.dobiasovsky.michal.stargate to javafx.fxml;
    exports net.dobiasovsky.michal.stargate;

    exports net.dobiasovsky.michal.stargate.score;
}
