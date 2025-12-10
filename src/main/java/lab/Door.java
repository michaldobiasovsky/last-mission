// java
// src/main/java/lab/Door.java
package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Door extends Entity {
    private static final double DEFAULT_WIDTH = 40;
    private static final double DEFAULT_HEIGHT = 60;

    private final double width;
    private final double height;
    private final DoorType type;

    public Door(double x, double y, DoorType type) {
        super(x, y);
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.type = type;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    public DoorType getType() {
        return type;
    }

    @Override
    public void draw(GraphicsContext gc) {
        Color fill = (type == DoorType.EXIT) ? Color.DARKMAGENTA : Color.DEEPSKYBLUE;
        Color stroke = Color.BLACK;

        gc.setFill(fill);
        gc.fillRect(getX(), getY(), width, height);

        gc.setStroke(stroke);
        gc.strokeRect(getX(), getY(), width, height);
    }
}
