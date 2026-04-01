module net.dobiasovsky.michal.stargate {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.logging;
    requires org.apache.logging.log4j;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires com.h2database;
    requires static lombok;

    opens net.dobiasovsky.michal.stargate to javafx.fxml;
    opens net.dobiasovsky.michal.stargate.score to org.hibernate.orm.core, jakarta.persistence;
    exports net.dobiasovsky.michal.stargate;

    exports net.dobiasovsky.michal.stargate.score;
}
