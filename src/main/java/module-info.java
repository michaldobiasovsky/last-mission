module net.dobiasovsky.lastmission {
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

    opens net.dobiasovsky.lastmission to javafx.fxml;
    opens net.dobiasovsky.lastmission.score to org.hibernate.orm.core, jakarta.persistence;
    exports net.dobiasovsky.lastmission;

    exports net.dobiasovsky.lastmission.score;
}
