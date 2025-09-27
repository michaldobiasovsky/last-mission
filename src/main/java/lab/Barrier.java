package lab;

public class Barrier {
    private double x;
    private double y;
    private double width;
    private double height;

    public Barrier(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.BROWN);
        gc.fillRect(x, y, width, height);
    }

}
