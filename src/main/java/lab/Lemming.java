// src/main/java/lab/Lemming.java
package lab;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Lemming extends Entity {

    private static final double SCALE = 0.7;

    private static final double GRAVITY = 400;
    public static volatile double speedMultiplier = 1.0;

    private static final Image WALK_RIGHT = new Image(Lemming.class.getResourceAsStream("/lab/cosmo_right.gif"));
    private static final Image WALK_LEFT = new Image(Lemming.class.getResourceAsStream("/lab/cosmo_left.gif"));
    private static final Image BLOCK_IMG = new Image(Lemming.class.getResourceAsStream("/lab/stop.gif"));

    private final double width;
    private final double height;

    private Role role;
    private int direction;
    private double velocityY;
    private final double speedX;

    private double directionCooldown = 0;
    private static final double DIRECTION_COOLDOWN_SEC = 0.15;

    public Lemming(double x, double y) {
        super(x, y);
        // Výpočet rozměrů podle obrázku a měřítka
        this.width = WALK_RIGHT.getWidth() * SCALE;
        this.height = WALK_RIGHT.getHeight() * SCALE;

        this.direction = 1;
        this.velocityY = 0;
        this.role = Role.DEFAULT;
        this.speedX = 40;
    }

    @Override
    public double getWidth() { return width; }

    @Override
    public double getHeight() { return height; }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(getX(), getY(), width, height);
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

        int totalSteps = steps;
        double stepWidth = width * 0.5;
        double stepHeight = height * 0.5;

        for (int i = 0; i < totalSteps; i++) {
            double xi = startX + direction * (i + 1) * (stepWidth - overlapX / 2);
            double yi = startY + (i * stepHeight);

            boolean exists = world.getBarriers().stream()
                .anyMatch(b -> Math.abs(b.getX() - xi) < stepWidth && Math.abs(b.getY() - yi) < stepHeight * 0.5);
            if (!exists) {
                world.getBarriers().add(new Step(xi, yi, stepWidth, stepHeight, direction));
            }
        }
    }


    public void checkCollisionWithBarrier(Barrier barrier, World world) {
        double x = getX();
        double y = getY();
        double w = getWidth();
        double h = getHeight();

        if (x >= barrier.getX() + barrier.getWidth() || x + w <= barrier.getX() ||
            y >= barrier.getY() + barrier.getHeight() || y + h <= barrier.getY()) {
            return;
        }

        double barrierTop = barrier.getY() + barrier.getHeight();
        double barrierBottom = barrier.getY();
        double barrierLeft = barrier.getX();
        double barrierRight = barrier.getX() + barrier.getWidth();

        if (barrier.isStep()) {
            Step step = (Step) barrier;
            int stepDir = step.getStepDirection();
            boolean canClimb = (direction == stepDir);

            double heightDifference = barrierTop - y;

            boolean isTooHigh = heightDifference > 15;

            if (canClimb && !isTooHigh && y < barrierTop && y + h > barrierBottom) {

                double targetY = barrierTop;
                Rectangle2D targetBox = new Rectangle2D(x, targetY, w, h);
                boolean isCeilingBlocked = world.getBarriers().stream()
                    .filter(b -> !b.isStep())
                    .anyMatch(b -> b.getBoundingBox().intersects(targetBox));

                if (!isCeilingBlocked) {
                    position = new Point2D(x, barrierTop);
                    velocityY = 0;
                    return;
                }
            }

        }

        if (velocityY > 0) {
            double penetration = (y + h) - barrierBottom;
            if (penetration > 0 && y < barrierBottom) {
                position = new Point2D(x, barrierBottom - h);
                velocityY = 0;
                return;
            }
        }

        if (velocityY <= 0) {
            double overlap = barrierTop - y;
            if (overlap > 0 && overlap < h * 0.6) {
                position = new Point2D(x, barrierTop);
                velocityY = 0;
                return;
            }
        }

        double frontX = (direction == 1) ? (x + w) : x;
        boolean hitsRight = (direction == 1 && frontX >= barrierLeft && frontX < barrierLeft + 15);
        boolean hitsLeft  = (direction == -1 && frontX <= barrierRight && frontX > barrierRight - 15);
        boolean verticallyInside = (y + h - 2 > barrierBottom) && (y < barrierTop - 2);

        if ((hitsRight || hitsLeft) && verticallyInside) {
            changeDirection();
            if (direction == 1) position = new Point2D(barrierRight + 1, y);
            else position = new Point2D(barrierLeft - w - 1, y);
        }
    }

    private void checkCollisionWithLemming(Lemming other) {
        if (other.getRole() != Role.BLOCK) return;

        double x = getX();
        double y = getY();
        double ox = other.getX();
        double oy = other.getY();
        double ow = other.getWidth();
        double oh = other.getHeight();

        boolean overlapX = x < ox + ow && x + width > ox;
        boolean overlapY = y < oy + oh && y + height > oy;
        if (!overlapX || !overlapY) return;

        if (y + height - 2 > oy && y < oy + oh - 2) {
            changeDirection();
            if (direction == 1) {
                position = new Point2D(ox + ow + 1, y);
            } else {
                position = new Point2D(ox - width - 1, y);
            }
        }

        if (velocityY <= 0 && y < oy + oh && y + height > oy + oh) {
            position = new Point2D(x, oy + oh);
            velocityY = 0;
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        double x = getX();
        double y = getY();

        Image img = (role == Role.BLOCK) ? BLOCK_IMG : ((direction == 1) ? WALK_RIGHT : WALK_LEFT);

        gc.save();
        gc.scale(1, -1);
        gc.drawImage(img, x, -y - height, width, height);
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
