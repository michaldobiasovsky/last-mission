package lab;

    import javafx.geometry.Point2D;

    public class Lemming {
        public static final double WIDTH = 20;
        public static final double HEIGHT = 20;

        private Point2D position;
        private int direction; // 1 - vpravo, -1 - vlevo

        public Lemming(double x, double y) {
            this.position = new Point2D(x, y);
            this.direction = 1;
        }

        public double getX() { return position.getX(); }
        public double getY() { return position.getY(); }
        public int getDirection() { return direction; }
        public void changeDirection() { direction *= -1; }
        public void move(double distance) {
            position = position.add(distance * direction, 0);
        }

        public void checkCollision(Barrier barrier) {
            double x = position.getX();
            double y = position.getY();
            if (x < barrier.getX() + barrier.getWidth() && x + WIDTH > barrier.getX() &&
                y < barrier.getY() + barrier.getHeight() && y + HEIGHT > barrier.getY()) {
                changeDirection();
                if (direction == 1) {
                    position = new Point2D(barrier.getX() + barrier.getWidth(), y);
                } else {
                    position = new Point2D(barrier.getX() - WIDTH, y);
                }
            }
        }

        public void draw(javafx.scene.canvas.GraphicsContext gc) {
            double x = position.getX();
            double y = position.getY();
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
