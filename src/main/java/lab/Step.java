package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Step extends Barrier {

    // Směr schodů: 1 = schody jdou doprava (vylézá se zleva), -1 = schody jdou doleva
    private final int stepDirection;

    public Step(double x, double y, double lemmingWidth, double lemmingHeight, int direction) {
        super(x, y, lemmingWidth + 10, lemmingHeight);
        this.stepDirection = direction;
    }

    @Override
    public boolean isStep() {
        return true;
    }

    public int getStepDirection() {
        return stepDirection;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
    }
}
