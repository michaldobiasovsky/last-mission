package lab;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;

public class Lemming extends Entity {

    public static final double WIDTH = 20;
    public static final double HEIGHT = 20;
    private static final double GRAVITY = 400;
    public static volatile double speedMultiplier = 1.0;

    private Role role;
    private int direction;
    private double velocityY;
    private final double speedX;

    // Per-instance cooldown v sekundách simulace
    private double directionCooldown = 0;
    private static final double DIRECTION_COOLDOWN_SEC = 0.15;

    public Lemming(double x, double y) {
        super(x, y);
        this.direction = 1;
        this.velocityY = 0;
        this.role = Role.DEFAULT;
        this.speedX = 40;
    }

    @Override
    public double getWidth() { return WIDTH; }

    @Override
    public double getHeight() { return HEIGHT; }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(getX(), getY(), WIDTH, HEIGHT);
    }

    public int getDirection() { return direction; }

    public void changeDirection() {
        if (directionCooldown > 0) return;
        direction *= -1;
        directionCooldown = DIRECTION_COOLDOWN_SEC;
    }

    public Role getRole() { return role; }
    public void setRole(Role r) { this.role = r; }

    public void move(double dist) {
        position = position.add(dist * direction, 0);
    }

    public void becomeBlock() {
        this.role = Role.BLOCK;
        this.velocityY = 0;
    }

    public void buildStairs(World world, int steps) {
        if (steps <= 0) return;
        double startX = getX();
        double startY = getY();

        double overlapX = 5;

        for (int i = 0; i < steps; i++) {
            double xi = startX + direction * (i + 1) * (WIDTH - overlapX / 2);
            double yi = startY + (i * HEIGHT);

            boolean exists = world.getBarriers().stream()
                .anyMatch(b -> Math.abs(b.getX() - xi) < WIDTH && Math.abs(b.getY() - yi) < HEIGHT * 0.5);
            if (!exists) {
                // Předat směr schodů (stejný jako směr lemminga při stavbě)
                world.getBarriers().add(new Step(xi, yi, direction));
            }
        }
    }



    private boolean checkBarrierAt(World world, double bx, double by) {
        return world.getBarriers().stream()
            .anyMatch(b -> Math.abs(b.getX() - bx) < 0.1 && Math.abs(b.getY() - by) < 0.1);
    }

    public void checkCollisionWithBarrier(Barrier barrier, World world) {
        double x = getX();
        double y = getY();

        boolean overlapX = x < barrier.getX() + barrier.getWidth() && x + WIDTH > barrier.getX();
        boolean overlapY = y < barrier.getY() + barrier.getHeight() && y + HEIGHT > barrier.getY();

        if (!overlapX || !overlapY) return;

        double barrierTop = barrier.getY() + barrier.getHeight();
        double barrierBottom = barrier.getY();
        double barrierLeft = barrier.getX();
        double barrierRight = barrier.getX() + barrier.getWidth();

        // SCHODY: lemming vyleze jen když jde ve správném směru
        if (barrier.isStep()) {
            Step step = (Step) barrier;
            int stepDir = step.getStepDirection();

            // Lemming může vylézt pouze když jde STEJNÝM směrem jako schody
            boolean canClimb = (direction == stepDir);

            if (canClimb && y < barrierTop && y + HEIGHT > barrierBottom) {
                // Posunout lemminga NA vrchol schodu
                position = new Point2D(x, barrierTop);
                velocityY = 0;
                return;
            }

            // Padání shora — stání na schodu
            if (velocityY <= 0) {
                double overlap = barrierTop - y;
                if (overlap > 0 && overlap < HEIGHT * 0.6) {
                    position = new Point2D(x, barrierTop);
                    velocityY = 0;
                    return;
                }
            }

            // Jde opačným směrem — schod funguje jako zeď
            if (!canClimb) {
                double frontX = direction == 1 ? x + WIDTH : x;
                boolean hitsWall = (direction == 1 && frontX >= barrierLeft && frontX <= barrierLeft + 10)
                    || (direction == -1 && frontX <= barrierRight && frontX >= barrierRight - 10);

                if (hitsWall && y + HEIGHT - 2 > barrierBottom && y < barrierTop - 2) {
                    changeDirection();
                    if (direction == 1) {
                        position = new Point2D(barrierRight + 1, y);
                    } else {
                        position = new Point2D(barrierLeft - WIDTH - 1, y);
                    }
                }
            }
            return;
        }

        // BĚŽNÁ BARIÉRA: stání na ní
        if (velocityY <= 0) {
            double overlap = barrierTop - y;
            if (overlap > 0 && overlap < HEIGHT * 0.6) {
                position = new Point2D(x, barrierTop);
                velocityY = 0;
                return;
            }
        }

        // BOČNÍ KOLIZE pro ne-schody
        double frontX = direction == 1 ? x + WIDTH : x;
        boolean hitsWall = (direction == 1 && frontX >= barrierLeft && frontX <= barrierLeft + 10)
            || (direction == -1 && frontX <= barrierRight && frontX >= barrierRight - 10);

        if (hitsWall && y + HEIGHT - 2 > barrierBottom && y < barrierTop - 2) {
            changeDirection();
            if (direction == 1) {
                position = new Point2D(barrierRight + 1, y);
            } else {
                position = new Point2D(barrierLeft - WIDTH - 1, y);
            }
        }
    }



    private void checkCollisionWithLemming(Lemming other) {
        if (other.getRole() != Role.BLOCK) return;

        double x = getX();
        double y = getY();
        double ox = other.getX();
        double oy = other.getY();

        boolean overlapX = x < ox + WIDTH && x + WIDTH > ox;
        boolean overlapY = y < oy + HEIGHT && y + HEIGHT > oy;
        if (!overlapX || !overlapY) return;

        // Boční kolize
        if (y + HEIGHT - 2 > oy && y < oy + HEIGHT - 2) {
            changeDirection();
            if (direction == 1) {
                position = new Point2D(ox + WIDTH + 1, y);
            } else {
                position = new Point2D(ox - WIDTH - 1, y);
            }
        }

        // Stání na blokujícím lemmingovi
        if (velocityY <= 0 && y < oy + HEIGHT && y + HEIGHT > oy + HEIGHT) {
            position = new Point2D(x, oy + HEIGHT);
            velocityY = 0;
        }
    }

    @Override
    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        double x = getX();
        double y = getY();
        gc.setFill(role == Role.BLOCK ? Color.RED : Color.GREEN);
        gc.fillOval(x, y, WIDTH, HEIGHT);
        gc.setFill(Color.BLACK);
        if (direction == 1) {
            gc.fillOval(x + WIDTH * 0.6, y + HEIGHT * 0.25, 3, 3);
            gc.fillOval(x + WIDTH * 0.75, y + HEIGHT * 0.25, 3, 3);
        } else {
            gc.fillOval(x + WIDTH * 0.15, y + HEIGHT * 0.25, 3, 3);
            gc.fillOval(x + WIDTH * 0.30, y + HEIGHT * 0.25, 3, 3);
        }
    }

    public void simulate(double deltaTime, World world) {
        if (directionCooldown > 0) {
            directionCooldown -= deltaTime;
        }

        if (role == Role.BLOCK) {
            velocityY = 0;
            return;
        }

        move(speedX * speedMultiplier * deltaTime);
        velocityY -= GRAVITY * deltaTime;
        position = position.add(0, velocityY * deltaTime);

        for (Barrier barrier : world.getBarriers()) {
            checkCollisionWithBarrier(barrier, world);
        }

        for (Lemming other : world.getLemmings()) {
            if (other != this) {
                checkCollisionWithLemming(other);
            }
        }
    }

    public void onCollision(Lemming other) {

    }
}
