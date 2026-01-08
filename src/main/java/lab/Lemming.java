package lab;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lemming extends Entity implements DrawableSimulable{

    private final Logger logger;
    private final double scale;
    private final double gravity;
    private final double speedMultiplier;
    private final double collisionTolerance;
    private final double climbTolerance;
    private final double directionCooldownSec;

    private final Image walkRight;
    private final Image walkLeft;
    private final Image blockImg;
    private final AudioClip weeSound;

    private double width;
    private double height;
    private Role role;
    private int direction;
    private double velocityY;
    private final double speedX;
    private final double soundVolume;

    private double directionCooldown = 0;
    private boolean onGround = false;
    private boolean onFloor = false;
    private boolean hasScreamed = false;
    private boolean hasTouchedGroundOnce = false;

    public Lemming(double x, double y) {
        super(x, y);
        this.logger = Logger.getLogger(Lemming.class.getName());
        this.scale = 0.7;
        this.gravity = 400;
        this.speedMultiplier = 0.8;
        this.collisionTolerance = 15.0;
        this.climbTolerance = 2.0;
        this.directionCooldownSec = 0.15;

        this.walkRight = loadImage("/lab/cosmo_right.gif");
        this.walkLeft = loadImage("/lab/cosmo_left.gif");
        this.blockImg = loadImage("/lab/stop.gif");
        this.weeSound = loadSound("/lab/wee.mp3");

        this.direction = 1;
        this.velocityY = 0;
        this.role = Role.DEFAULT;
        this.speedX = 40;
        this.soundVolume = 0.2;
        this.width = walkRight.getWidth() * scale;
        this.height = walkRight.getHeight() * scale;
    }

    private Image loadImage(String path) {
        InputStream stream = Lemming.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalArgumentException("Image not found: " + path);
        }
        return new Image(stream);
    }

    private AudioClip loadSound(String path) {
        URL url = Lemming.class.getResource(path);
        if (url == null) {
            logger.log(Level.SEVERE, "Sound not found: {0}", path);
            return null;
        }
        return new AudioClip(url.toExternalForm());
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(getX(), getY(), getWidth(), getHeight());
    }

    public void changeDirection() {
        if (directionCooldown > 0) return;
        direction *= -1;
        directionCooldown = directionCooldownSec;
    }

    public Role getRole() { return role; }

    public void setRole(Role r) {
        this.role = r;
        if (r == Role.BLOCK) {
            this.width = blockImg.getWidth() * scale;
        } else {
            this.width = walkRight.getWidth() * scale;
            this.height = walkRight.getHeight() * scale;
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
        boolean isTooHigh = heightDifference > collisionTolerance;

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
        boolean hitsRight = (direction == 1 && frontX >= barrierLeft && frontX < barrierLeft + collisionTolerance);
        boolean hitsLeft  = (direction == -1 && frontX <= barrierRight && frontX > barrierRight - collisionTolerance);
        boolean verticallyInside = (y + h - climbTolerance > barrierBottom) && (y < barrierTop - climbTolerance);

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
            return blockImg;
        }
        return (direction == 1) ? walkRight : walkLeft;
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

        move(speedX * speedMultiplier * deltaTime);
        velocityY -= gravity * deltaTime;
        position = position.add(0, velocityY * deltaTime);

        if (velocityY < -150 && !onGround && !hasScreamed && hasTouchedGroundOnce) {
            if (weeSound != null) {
                weeSound.play(soundVolume);
            }
            hasScreamed = true;
        }

        for (Barrier barrier : world.getBarriers()) {
            checkCollisionWithBarrier(barrier, world);
        }

        for (Lemming other : world.getLemmings()) {
            if (other != this) {
                checkCollisionWithLemming(other);
            }
        }

        if (onGround) {
            hasScreamed = false;
            hasTouchedGroundOnce = true;
        }
    }
}
