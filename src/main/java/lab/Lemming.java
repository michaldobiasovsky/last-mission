package lab;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;

public class Lemming extends Entity {
    public static final double WIDTH = 20;
    public static final double HEIGHT = 20;
    private static final double GRAVITY = 400; // px/s^2

    public static volatile double speedMultiplier = 1.0;

    private Role role;

    private int direction; // 1 - right, -1 - left
    private double velocityY;
    private final double speedX;

    public Lemming(double x, double y) {
        super(x, y);
        this.direction = 1;
        this.velocityY = 0;
        this.role = Role.DEFAULT;
        this.speedX = 40;
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(getX(), getY(), WIDTH, HEIGHT);
    }

    public void onCollision(Lemming other) {
        System.out.println("collision");
    }

    public int getDirection() { return direction; }
    public void changeDirection() { direction *= -1; }
    public void move(double distance) {
        position = position.add(distance * direction, 0);
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public void becomeBlock() {
        this.role = Role.BLOCK;
        this.velocityY = 0;
    }

    public void buildStairs(World world, int steps) {
        if (steps <= 0) return;
        double baseX = getX();
        double baseY = getY();
        for (int i = 0; i < steps; i++) {
            double xi = baseX + direction * (i + 1) * WIDTH;
            double yi = baseY - i * HEIGHT;
            boolean exists = false;
            for (Barrier b : world.getBarriers()) {
                if (Math.abs(b.getX() - xi) < 1e-6 && Math.abs(b.getY() - yi) < 1e-6) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                world.getBarriers().add(new Step(xi, yi));
            }
        }
        becomeBlock();
    }

    public void checkCollisionWithBarrier(Barrier barrier) {
        double x = getX();
        double y = getY();
        if (x < barrier.getX() + barrier.getWidth() && x + WIDTH > barrier.getX() &&
            y < barrier.getY() + barrier.getHeight() && y + HEIGHT > barrier.getY()) {
            if (y + HEIGHT - 2 > barrier.getY() && y < barrier.getY() + barrier.getHeight() - 2) {
                changeDirection();
                if (direction == 1) {
                    position = new Point2D(barrier.getX() + barrier.getWidth(), y);
                } else {
                    position = new Point2D(barrier.getX() - WIDTH, y);
                }
            }

            if (velocityY < 0 && y < barrier.getY() + barrier.getHeight() && y + HEIGHT > barrier.getY() + barrier.getHeight()) {
                position = new Point2D(x, barrier.getY() + barrier.getHeight());
                velocityY = 0;
            }
        }
    }

    @Override
    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        double x = getX();
        double y = getY();
        if (role == Role.BLOCK) {
            gc.setFill(Color.RED);
        } else {
            gc.setFill(Color.GREEN);
        }
        gc.fillOval(x, y, WIDTH, HEIGHT);
        gc.setFill(Color.BLACK);
        if (direction == 1) {
            gc.fillOval(x + WIDTH * 0.6, y + HEIGHT * 0.25, 3, 3);
            gc.fillOval(x + WIDTH * 0.75, y + HEIGHT * 0.25, 3, 3);
        } else {
            gc.fillOval(x + WIDTH * 0.15, y + HEIGHT * 0.25, 3, 3);
            gc.fillOval(x + WIDTH * 0.3, y + HEIGHT * 0.25, 3, 3);
        }
    }

    public void simulate(double deltaTime, World world) {
        if (role == Role.BLOCK) {
            velocityY = 0;
            return;
        }
        move(speedX * speedMultiplier * deltaTime);
        velocityY -= GRAVITY * deltaTime;
        position = position.add(0, velocityY * deltaTime);

        for (Lemming other : world.getLemmings()) {
            if (other != this) {
                if (getBoundingBox().intersects(other.getBoundingBox())) {
                    if (other.getRole() == Role.BLOCK) {
                        checkCollisionWithLemming(other);
                    }
                    onCollision(other);
                }
            }
        }

        for (Barrier barrier : world.getBarriers()) {
            checkCollisionWithBarrier(barrier);
        }
    }

    private void checkCollisionWithLemming(Lemming other) {
        double x = getX();
        double y = getY();
        double ox = other.getX();
        double oy = other.getY();
        if (x < ox + WIDTH && x + WIDTH > ox &&
            y < oy + HEIGHT && y + HEIGHT > oy) {
            if (y + HEIGHT - 2 > oy && y < oy + HEIGHT - 2) {
                changeDirection();
                if (direction == 1) {
                    position = new Point2D(ox + WIDTH, y);
                } else {
                    position = new Point2D(ox - WIDTH, y);
                }
            }
            if (velocityY < 0 && y < oy + HEIGHT && y + HEIGHT > oy + HEIGHT) {
                position = new Point2D(x, oy + HEIGHT);
                velocityY = 0;
            }
        }
    }
}
