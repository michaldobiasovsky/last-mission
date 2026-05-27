package net.dobiasovsky.lastmission;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Lemming extends Entity implements DrawableSimulable{
    private static final double SCALE = 0.7;
    private static final Image WALK_RIGHT_TEMPLATE = loadSharedImage("cosmo_right.gif");
    private static final Image WALK_LEFT_TEMPLATE = loadSharedImage("cosmo_left.gif");
    private static final Image BLOCK_TEMPLATE = loadSharedImage("stop.gif");
    private static final AudioClip WEE_SOUND = loadSharedSound("wee.wav");
    private static final double DEFAULT_WIDTH = WALK_RIGHT_TEMPLATE.getWidth() * SCALE;
    private static final double DEFAULT_HEIGHT = WALK_RIGHT_TEMPLATE.getHeight() * SCALE;

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
    @Getter
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
    private volatile boolean running = false;
    private Thread behaviorThread;
    private volatile World world;

    public Lemming(double x, double y) {
        super(x, y);
        this.scale = 0.7;
        this.gravity = 400;
        this.speedMultiplier = 0.8;
        this.collisionTolerance = 15.0;
        this.climbTolerance = 2.0;
        this.directionCooldownSec = 0.15;

        this.walkRight = WALK_RIGHT_TEMPLATE;
        this.walkLeft = WALK_LEFT_TEMPLATE;
        this.blockImg = BLOCK_TEMPLATE;
        this.weeSound = WEE_SOUND;

        this.direction = 1;
        this.velocityY = 0;
        this.role = Role.DEFAULT;
        this.speedX = 40;
        this.soundVolume = 0.2;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }

    private static Image loadSharedImage(String path) {
        try (InputStream stream = Lemming.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalArgumentException("Image not found: " + path);
            }
            return new Image(stream);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Failed to load image: " + path, e);
        }
    }

    private static AudioClip loadSharedSound(String path) {
        URL url = Lemming.class.getResource(path);
        if (url == null) {
            return null;
        }
        return new AudioClip(url.toExternalForm());
    }

    public static double getDefaultSpawnWidth() {
        return DEFAULT_WIDTH;
    }

    public static double getDefaultSpawnHeight() {
        return DEFAULT_HEIGHT;
    }

    public synchronized void startBehavior(World world) {
        if (running) {
            return;
        }
        this.world = world;
        this.running = true;

        behaviorThread = new Thread(this::runBehaviorLoop, "Lemming-" + System.identityHashCode(this));
        behaviorThread.setDaemon(true);
        behaviorThread.start();
    }

    public synchronized void stopBehavior() {
        running = false;
        Thread thread = behaviorThread;
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void runBehaviorLoop() {
        long lastFrame = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            double delta = (now - lastFrame) / 1_000_000_000D;
            lastFrame = now;

            World currentWorld = world;
            if (currentWorld == null) {
                break;
            }

            simulate(delta, currentWorld);

            if (!running || !currentWorld.containsLemming(this)) {
                break;
            }

            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                break;
            }
        }
        running = false;
    }

    public synchronized void changeDirection() {
        if (directionCooldown > 0) return;
        direction *= -1;
        directionCooldown = directionCooldownSec;
    }

    public synchronized void setRole(Role r) {
        this.role = r;
        if (r == Role.BLOCK) {
            this.width = blockImg.getWidth() * scale;
        } else {
            this.width = walkRight.getWidth() * scale;
            this.height = walkRight.getHeight() * scale;
        }
    }

    public synchronized void move(double dist) {
        position = position.add(dist * direction, 0);
    }

    public synchronized boolean becomeBlock() {
        if (!onGround) return false;
        setRole(Role.BLOCK);
        this.velocityY = 0;
        return true;
    }

    public boolean buildStairs(World world, int steps) {
        List<Barrier> barrierSnapshot = world.snapshotBarriers();
        List<Step> stepsToAdd = new java.util.ArrayList<>();

        synchronized (this) {
            if (role == Role.BLOCK) return false;
            if (steps <= 0) return false;
            if (!onFloor) return false;

            double startX = getX();
            double startY = getY();
            double overlapX = 5;

            double stepWidth = getWidth() * 0.5;
            double stepHeight = getHeight() * 0.5;

            for (int i = 0; i < steps; i++) {
                double xi = startX + direction * (i + 1) * (stepWidth - overlapX / 2);
                double yi = startY + (i * stepHeight);

                boolean exists = barrierSnapshot.stream()
                    .anyMatch(b -> Math.abs(b.getX() - xi) < stepWidth && Math.abs(b.getY() - yi) < stepHeight * 0.5);

                if (!exists) {
                    stepsToAdd.add(new Step(xi, yi, stepWidth, stepHeight, direction));
                }
            }
        }

        for (Step step : stepsToAdd) {
            world.addBarrier(step);
        }

        return !stepsToAdd.isEmpty();
    }

    public void checkCollisionWithBarrier(Barrier barrier, List<Barrier> barrierSnapshot) {
        if (!isOverlapping(barrier)) {
            return;
        }

        if (handleStepCollision(barrier, barrierSnapshot)) {
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

    private boolean handleStepCollision(Barrier barrier, List<Barrier> barrierSnapshot) {
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

            boolean isCeilingBlocked = barrierSnapshot.stream()
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
    public synchronized void draw(GraphicsContext gc) {
        double x = getX();
        double y = getY();

        Image img = getCurrentImage();

        gc.save();
        gc.scale(1, -1);
        gc.drawImage(img, x, -y - height, getWidth(), getHeight());
        gc.restore();
    }

    private synchronized Image getCurrentImage() {
        if (role == Role.BLOCK) {
            return blockImg;
        }
        return (direction == 1) ? walkRight : walkLeft;
    }

    @Override
    public synchronized double getX() {
        return super.getX();
    }

    @Override
    public synchronized double getY() {
        return super.getY();
    }

    @Override
    public synchronized double getWidth() {
        return width;
    }

    @Override
    public synchronized double getHeight() {
        return height;
    }

    @Override
    public synchronized Rectangle2D getBoundingBox() {
        return new Rectangle2D(getX(), getY(), getWidth(), getHeight());
    }

    public void simulate(double deltaTime, World world) {
        List<Barrier> barrierSnapshot = world.snapshotBarriers();
        List<Lemming> lemmingSnapshot = world.snapshotLemmings();

        double previousX;
        double previousY;
        synchronized (this) {
            previousX = getX();
            previousY = getY();

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
        }

        synchronized (this) {
            for (Barrier barrier : barrierSnapshot) {
                checkCollisionWithBarrier(barrier, barrierSnapshot);
            }

            for (Lemming other : lemmingSnapshot) {
                if (other != this) {
                    checkCollisionWithLemming(other);
                }
            }

            if (onGround) {
                hasScreamed = false;
                hasTouchedGroundOnce = true;
            }

            log.trace("Lemming position changed from ({}, {}) to ({}, {})", previousX, previousY, getX(), getY());
        }
    }
}
