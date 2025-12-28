// java
// 'src/main/java/lab/Lemming.java'
package lab;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Lemming extends Entity {

    public static final double WIDTH = 20;
    public static final double HEIGHT = 20;
    private static final double GRAVITY = 400;
    public static volatile double speedMultiplier = 1.0;

    private static final Image WALK_RIGHT = new Image(
        Lemming.class.getResourceAsStream("/lab/cosmo_right.gif"),
        WIDTH, HEIGHT, true, true
    );
    private static final Image WALK_LEFT = new Image(
        Lemming.class.getResourceAsStream("/lab/cosmo_left.gif"),
        WIDTH, HEIGHT, true, true
    );
    private static final Image BLOCK_IMG = new Image(
        Lemming.class.getResourceAsStream("/lab/stop.gif"),
        WIDTH, HEIGHT, true, true
    );

    private Role role;
    private int direction;
    private double velocityY;
    private final double speedX;

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
                world.getBarriers().add(new Step(xi, yi, direction));
            }
        }
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

        if (barrier.isStep()) {
            Step step = (Step) barrier;
            int stepDir = step.getStepDirection();
            boolean canClimb = (direction == stepDir);

            if (canClimb && y < barrierTop && y + HEIGHT > barrierBottom) {
                position = new Point2D(x, barrierTop);
                velocityY = 0;
                return;
            }

            if (velocityY <= 0) {
                double overlap = barrierTop - y;
                if (overlap > 0 && overlap < HEIGHT * 0.6) {
                    position = new Point2D(x, barrierTop);
                    velocityY = 0;
                    return;
                }
            }

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

        if (velocityY <= 0) {
            double overlap = barrierTop - y;
            if (overlap > 0 && overlap < HEIGHT * 0.6) {
                position = new Point2D(x, barrierTop);
                velocityY = 0;
                return;
            }
        }

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

        if (y + HEIGHT - 2 > oy && y < oy + HEIGHT - 2) {
            changeDirection();
            if (direction == 1) {
                position = new Point2D(ox + WIDTH + 1, y);
            } else {
                position = new Point2D(ox - WIDTH - 1, y);
            }
        }

        if (velocityY <= 0 && y < oy + HEIGHT && y + HEIGHT > oy + HEIGHT) {
            position = new Point2D(x, oy + HEIGHT);
            velocityY = 0;
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        double x = getX();
        double y = getY();

        Image img = (role == Role.BLOCK) ? BLOCK_IMG : ((direction == 1) ? WALK_RIGHT : WALK_LEFT);

        // \- svět je už invertovaný (scale(1, \-1)), tak lemminga lokálně vrátíme zpět
        gc.save();
        gc.scale(1, -1);
        gc.drawImage(img, x, -y - HEIGHT, WIDTH, HEIGHT);
        gc.restore();
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
