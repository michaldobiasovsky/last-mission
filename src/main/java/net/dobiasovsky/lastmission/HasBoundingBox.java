package net.dobiasovsky.lastmission;

import javafx.geometry.Rectangle2D;

public interface HasBoundingBox {
    Rectangle2D getBoundingBox();
    double getX();
    double getY();
}
