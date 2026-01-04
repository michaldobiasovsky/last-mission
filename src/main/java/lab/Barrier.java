package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class Barrier extends Entity {
    private final double width;
    private final double height;
    private final Image wallTexImage;

    public Barrier(double x, double y, double width, double height) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.wallTexImage = new Image(Barrier.class.getResourceAsStream("/lab/texture.jpg"));
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
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
