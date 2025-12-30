package lab;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.InputStream;

public class Lemming extends Entity {

    private static final double SCALE = 0.7;
    private static final double GRAVITY = 400;

    private static final double SPEED_MULTIPLIER = 0.8;

    private static Image loadImage(String path) {
        InputStream stream = Lemming.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalArgumentException("Obrázek nebyl nalezen: " + path);
        }
        return new Image(stream);
    }

    private static final Image WALK_RIGHT = loadImage("/lab/cosmo_right.gif");
    private static final Image WALK_LEFT = loadImage("/lab/cosmo_left.gif");
    private static final Image BLOCK_IMG = loadImage("/lab/stop.gif");

    private static final double COLLISION_TOLERANCE = 15.0;
    private static final double CLIMB_TOLERANCE = 2.0;

    private double width;
    private double height;

    private Role role;
    private int direction;
    private double velocityY;
    private final double speedX;

    private double directionCooldown = 0;
    private static final double DIRECTION_COOLDOWN_SEC = 0.15;

    private boolean onGround = false;
    private boolean onFloor = false;

    public Lemming(double x, double y) {
        super(x, y);
        this.direction = 1;
        this.velocityY = 0;
        this.role = Role.DEFAULT;
        this.speedX = 40;
        this.width = WALK_RIGHT.getWidth() * SCALE;
        this.height = WALK_RIGHT.getHeight() * SCALE;
    }

    @Override
    public double getWidth() { return width; }

    @Override
    public double getHeight() { return height; }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(getX(), getY(), getWidth(), getHeight());
    }

    public void changeDirection() {
        if (directionCooldown > 0) return;
        direction *= -1;
        directionCooldown = DIRECTION_COOLDOWN_SEC;
    }

    public Role getRole() { return role; }
    public void setRole(Role r) {
        this.role = r;
        if (r == Role.BLOCK) {
            this.width = BLOCK_IMG.getWidth() * SCALE;
        } else {
            this.width = WALK_RIGHT.getWidth() * SCALE;
            this.height = WALK_RIGHT.getHeight() * SCALE;
        }
    }

    public void move(double dist) {
        position = position.add(dist * direction, 0);
    }

    public boolean becomeBlock() {
        if (!onGround) return false;
        setRole(Role.BLOCK);
        this.velocityY = 0;
        return true;
    }

    public boolean buildStairs(World world, int steps) {
        if (role == Role.BLOCK) return false;
        if (steps <= 0) return false;
        if (!onFloor) return false;

        double startX = getX();
        double startY = getY();
        double overlapX = 5;

        double stepWidth = getWidth() * 0.5;
        double stepHeight = getHeight() * 0.5;

        boolean builtAny = false;

        for (int i = 0; i < steps; i++) {
            double xi = startX + direction * (i + 1) * (stepWidth - overlapX / 2);
            double yi = startY + (i * stepHeight);

            boolean exists = world.getBarriers().stream()
                .anyMatch(b -> Math.abs(b.getX() - xi) < stepWidth && Math.abs(b.getY() - yi) < stepHeight * 0.5);

            if (!exists) {
                world.getBarriers().add(new Step(xi, yi, stepWidth, stepHeight, direction));
                builtAny = true;
            }
        }

        return builtAny;
    }

    public void checkCollisionWithBarrier(Barrier barrier, World world) {
        if (!isOverlapping(barrier)) {
            return;
        }

        if (handleStepCollision(barrier, world)) {
            return;
        }

        if (handleVerticalCollision(barrier)) {
            return;
        }

        handleHorizontalCollision(barrier);
    }

    boolean isOverlapping(Barrier barrier) {
        double x = getX();
        double y = getY();
        double w = getWidth();
        double h = getHeight();

        return x < barrier.getX() + barrier.getWidth()
            && x + w > barrier.getX()
            && y < barrier.getY() + barrier.getHeight()
            && y + h > barrier.getY();
    }

    private boolean handleStepCollision(Barrier barrier, World world) {
        if (!barrier.isStep()) return false;

        Step step = (Step) barrier;
        double barrierTop = barrier.getY() + barrier.getHeight();
        double barrierBottom = barrier.getY();
        double h = getHeight();
        double y = getY();

        int stepDir = step.getStepDirection();
        boolean canClimb = (direction == stepDir);
        double heightDifference = barrierTop - y;
        boolean isTooHigh = heightDifference > COLLISION_TOLERANCE;

        if (canClimb && !isTooHigh && y < barrierTop && y + h > barrierBottom) {
            Rectangle2D targetBox = new Rectangle2D(getX(), barrierTop, getWidth(), h);

            boolean isCeilingBlocked = world.getBarriers().stream()
                .filter(b -> !b.isStep())
                .anyMatch(b -> b.getBoundingBox().intersects(targetBox));

            if (!isCeilingBlocked) {
                position = new Point2D(getX(), barrierTop);
                velocityY = 0;
                onGround = true;
                return true;
            }
        }
        return false;
    }

    private boolean handleVerticalCollision(Barrier barrier) {
        double barrierTop = barrier.getY() + barrier.getHeight();
        double barrierBottom = barrier.getY();
        double y = getY();
        double h = getHeight();
        double x = getX();

        // Falling down
        if (velocityY > 0) {
            double penetration = (y + h) - barrierBottom;
            if (penetration > 0 && y < barrierBottom) {
                position = new Point2D(x, barrierBottom - h);
                velocityY = 0;
                onGround = true;
                if (!barrier.isStep()) {
                    onFloor = true;
                }
                return true;
            }
        }

        // Jumping up (hitting ceiling)
        if (velocityY <= 0) {
            double overlap = barrierTop - y;
            if (overlap > 0 && overlap < h * 0.6) {
                position = new Point2D(x, barrierTop);
                velocityY = 0;
                onGround = true;
                if (!barrier.isStep()) {
                    onFloor = true;
                }
                return true;
            }
        }
        return false;
    }

    private void handleHorizontalCollision(Barrier barrier) {
        double barrierTop = barrier.getY() + barrier.getHeight();
        double barrierBottom = barrier.getY();
        double barrierLeft = barrier.getX();
        double barrierRight = barrier.getX() + barrier.getWidth();

        double x = getX();
        double y = getY();
        double w = getWidth();
        double h = getHeight();

        double frontX = (direction == 1) ? (x + w) : x;
        boolean hitsRight = (direction == 1 && frontX >= barrierLeft && frontX < barrierLeft + COLLISION_TOLERANCE);
        boolean hitsLeft  = (direction == -1 && frontX <= barrierRight && frontX > barrierRight - COLLISION_TOLERANCE);
        boolean verticallyInside = (y + h - CLIMB_TOLERANCE > barrierBottom) && (y < barrierTop - CLIMB_TOLERANCE);

        if ((hitsRight || hitsLeft) && verticallyInside) {
            changeDirection();
            if (direction == 1) {
                position = new Point2D(barrierRight + 1, y);
            } else {
                position = new Point2D(barrierLeft - w - 1, y);
            }
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

        double w = getWidth();
        double h = getHeight();

        boolean overlapX = x < ox + ow && x + w > ox;
        boolean overlapY = y < oy + oh && y + h > oy;
        if (!overlapX || !overlapY) return;

        if (y + h - 2 > oy && y < oy + oh - 2) {
            changeDirection();
            if (direction == 1) position = new Point2D(ox + ow + 1, y);
            else position = new Point2D(ox - w - 1, y);
        }

        if (velocityY <= 0 && y < oy + oh && y + h > oy + oh) {
            position = new Point2D(x, oy + oh);
            velocityY = 0;
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        double x = getX();
        double y = getY();

        Image img = getCurrentImage();

        gc.save();
        gc.scale(1, -1);
        gc.drawImage(img, x, -y - height, getWidth(), getHeight());
        gc.restore();
    }

    private Image getCurrentImage() {
        if (role == Role.BLOCK) {
            return BLOCK_IMG;
        }
        return (direction == 1) ? WALK_RIGHT : WALK_LEFT;
    }

    public void simulate(double deltaTime, World world) {
        if (directionCooldown > 0) {
            directionCooldown -= deltaTime;
        }

        if (role == Role.BLOCK) {
            velocityY = 0;
            return;
        }

        onGround = false;
        onFloor = false;

        move(speedX * SPEED_MULTIPLIER * deltaTime);
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
}
