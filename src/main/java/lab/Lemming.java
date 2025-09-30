package lab;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Lemming {
    public static final double WIDTH = 20;
    public static final double HEIGHT = 20;
    private static final double GRAVITY = 400; // px/s^2

    private Role role;

    private Point2D position;
    private int direction; // 1 - vpravo, -1 - vlevo
    private double velocityY;
    private final double speedX;

    public Lemming(double x, double y) {
        this.position = new Point2D(x, y);
        this.direction = 1;
        this.velocityY = 0;
        this.role = Role.DEFAULT;
        this.speedX = 40; // počáteční rychlost v ose X
    }


    public double getX() { return position.getX(); }
    public double getY() { return position.getY(); }
    public int getDirection() { return direction; }
    public void changeDirection() { direction *= -1; }
    public void move(double distance) {
        position = position.add(distance * direction, 0);
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public void checkCollision(Barrier barrier) {
        double x = position.getX();
        double y = position.getY();
        if (x < barrier.getX() + barrier.getWidth() && x + WIDTH > barrier.getX() &&
            y < barrier.getY() + barrier.getHeight() && y + HEIGHT > barrier.getY()) {
            // kolize z boku
            if (y + HEIGHT - 2 > barrier.getY() && y < barrier.getY() + barrier.getHeight() - 2) {
                changeDirection();
                if (direction == 1) {
                    position = new Point2D(barrier.getX() + barrier.getWidth(), y);
                } else {
                    position = new Point2D(barrier.getX() - WIDTH, y);
                }
            }
            // kolize se zemí (shora) - pokud y roste nahoru a gravitace je záporná
            if (velocityY < 0 && y < barrier.getY() + barrier.getHeight() && y + HEIGHT > barrier.getY() + barrier.getHeight()) {
                position = new Point2D(x, barrier.getY() + barrier.getHeight());
                velocityY = 0;
            }
        }
    }
    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        double x = position.getX();
        double y = position.getY();
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
        move(speedX * deltaTime);
        velocityY -= GRAVITY * deltaTime;
        position = position.add(0, velocityY * deltaTime);

        for (Lemming other : world.getLemmings()) {
            if (other != this && other.getRole() == Role.BLOCK) {
                checkCollisionWithLemming(other);
            }
        }

        for (Barrier barrier : world.getBarriers()) {
            checkCollision(barrier);
        }
    }


    // Kontrola kolize s jiným lemmingem (funguje jako bariéra)
    private void checkCollisionWithLemming(Lemming other) {
        double x = position.getX();
        double y = position.getY();
        double ox = other.getX();
        double oy = other.getY();
        if (x < ox + WIDTH && x + WIDTH > ox &&
            y < oy + HEIGHT && y + HEIGHT > oy) {
            // kolize z boku
            if (y + HEIGHT - 2 > oy && y < oy + HEIGHT - 2) {
                changeDirection();
                if (direction == 1) {
                    position = new Point2D(ox + WIDTH, y);
                } else {
                    position = new Point2D(ox - WIDTH, y);
                }
            }
            // kolize shora (stejná logika jako s bariérou)
            if (velocityY < 0 && y < oy + HEIGHT && y + HEIGHT > oy + HEIGHT) {
                position = new Point2D(x, oy + HEIGHT);
                velocityY = 0;
            }
        }
    }
}
