package net.dobiasovsky.michal.stargate;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import lombok.Getter;

public class Barrier extends Entity {
    @Getter
    private final double width;
    @Getter
    private final double height;
    private final Image wallTexImage;

    public Barrier(double x, double y, double width, double height) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.wallTexImage = new Image(Barrier.class.getResourceAsStream("texture.jpg"));
    }


    public boolean isStep() {
        return false;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double w = getWidth();
        double h = getHeight();

        double tile = Math.clamp(Math.min(w, h), 1.0, Double.POSITIVE_INFINITY);
        ImagePattern pattern = new ImagePattern(wallTexImage, getX(), getY(), tile, tile, false);

        gc.setFill(pattern);
        gc.fillRect(getX(), getY(), w, h);
    }
}
