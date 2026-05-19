package net.dobiasovsky.lastmission;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class DrawingThread extends AnimationTimer {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final World world;


    private double x = 0;
    private double y = 50;
    private double speedX = 150;
    private double speedY = 0;
    private long lastFrame = 0;
    private boolean lastFrameXDirectionChanged = false;
    private boolean lastFrameYDirectionChanged = false;

    public DrawingThread(Canvas canvas, World world) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.world = world;
    }

    @Override
    public void handle(long now) {
        double delta = lastFrame == 0 ? 0 : (now - lastFrame) / 1_000_000_000D;
        lastFrame = now;
        gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight());
        world.draw(gc);


        world.simulate(delta);
        x += delta * speedX;
        y += delta * speedY;
        speedY += delta * 9.81 * 2;
        if (!lastFrameXDirectionChanged && (x < 0 || x > canvas.getWidth() - 20)) {
            speedX *= -0.9;
            lastFrameXDirectionChanged = true;
        } else {
            lastFrameXDirectionChanged = false;
        }
        if (!lastFrameYDirectionChanged && (y < 0 || y > canvas.getHeight() - 20)) {
            speedY *= -0.8;
            lastFrameYDirectionChanged = true;
        } else {
            lastFrameYDirectionChanged = false;
        }
    }
}
