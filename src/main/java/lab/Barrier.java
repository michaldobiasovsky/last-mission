package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Barrier extends Entity {
    private final double width;
    private final double height;

    public Barrier(double x, double y, double width, double height) {
        super(x, y);
        this.width = width;
        this.height = height;
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
        gc.setFill(Color.BROWN);
        gc.fillRect(getX(), getY(), width, height);
    }
}
