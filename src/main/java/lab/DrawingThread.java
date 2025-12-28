package lab;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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

        drawFps(delta);
        world.simulate(delta);
        x += delta * speedX;
        y += delta * speedY;
        speedY += delta * 9.81 * 2; //gravity
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

    private void drawFps(double delta) {
        int fps = calcFps(delta);
        gc.setFont(new Font("Arial", 30));
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("FPS: %04d", fps), 10, canvas.getHeight() - 10);
    }

    private double fpsSum = 0;
    private double fpsCount = 0;
    private int avergeFps = 0;

    private int calcFps(double delta) {
        fpsSum += 1 / delta;
        fpsCount += 1;
        if (fpsCount >= 100) {
            avergeFps = (int) (fpsSum / fpsCount);
            fpsSum = 0;
            fpsCount = 0;
        }
        return avergeFps;
    }
}
