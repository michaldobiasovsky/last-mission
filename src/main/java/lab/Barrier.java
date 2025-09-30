package lab;

import javafx.geometry.Point2D;

public class Barrier {
    private Point2D position;
    private double width;
    private double height;

    public Barrier(double x, double y, double width, double height) {
        this.position = new Point2D(x, y);
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.BROWN);
        gc.fillRect(position.getX(), position.getY(), width, height);
    }
}
