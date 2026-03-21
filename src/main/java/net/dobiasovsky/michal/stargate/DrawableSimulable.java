package net.dobiasovsky.michal.stargate;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public interface DrawableSimulable {
    void draw(GraphicsContext gc);

    void simulate(double deltaTime, World world);
    Rectangle2D getBoundingBox();
}
