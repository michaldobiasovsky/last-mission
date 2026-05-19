package net.dobiasovsky.lastmission;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class DrawingThread extends AnimationTimer {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final World world;
    private long lastFrame = 0;

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
    }
}
