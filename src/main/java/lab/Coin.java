package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Coin extends Entity {
    public static final double SIZE = 15;
    private boolean collected = false;

    public Coin(double x, double y) {
        super(x, y);
    }

    @Override
    public double getWidth() {
        return SIZE;
    }

    @Override
    public double getHeight() {
        return SIZE;
    }

    public boolean isCollected() {
        return collected;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (!collected) {
            gc.setFill(Color.GOLD);
            gc.fillOval(getX(), getY(), SIZE, SIZE);
            gc.setStroke(Color.ORANGE);
            gc.strokeOval(getX(), getY(), SIZE, SIZE);
        }
    }

    public void onCollision() {
        collected = true;
    }
}
