package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Step extends Barrier {
    public Step(double x, double y) {
        super(x, y, Lemming.WIDTH, Lemming.HEIGHT);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
    }
}
