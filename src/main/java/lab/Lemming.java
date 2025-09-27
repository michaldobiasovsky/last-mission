package lab;

public class Lemming {
    public static final double WIDTH = 20;
    public static final double HEIGHT = 20;

    private double x;
    private double y;
    private int direction; // 1 - vpravo, -1 - vlevo

    public Lemming(double x, double y) {
        this.x = x;
        this.y = y;
        this.direction = 1;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getDirection() { return direction; }
    public void changeDirection() { direction *= -1; }
    public void move(double distance) { x += distance * direction; }

    public void checkCollision(Barrier barrier) {
        if (x < barrier.getX() + barrier.getWidth() && x + WIDTH > barrier.getX() &&
            y < barrier.getY() + barrier.getHeight() && y + HEIGHT > barrier.getY()) {
            changeDirection();
            if (direction == 1) {
                x = barrier.getX() + barrier.getWidth();
            } else {
                x = barrier.getX() - WIDTH;
            }
        }
    }

    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.fillOval(x, y, WIDTH, HEIGHT);
        gc.setFill(javafx.scene.paint.Color.BLACK);
        if (direction == 1) {
            gc.fillOval(x + WIDTH * 0.6, y + HEIGHT * 0.25, 3, 3);
            gc.fillOval(x + WIDTH * 0.75, y + HEIGHT * 0.25, 3, 3);
        } else {
            gc.fillOval(x + WIDTH * 0.15, y + HEIGHT * 0.25, 3, 3);
            gc.fillOval(x + WIDTH * 0.3, y + HEIGHT * 0.25, 3, 3);
        }
    }

    public void simulate(double deltaTime) {
        move(50 * deltaTime);
    }
}
