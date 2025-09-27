package lab;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class World {

    private double width;
    private double height;

    private Lemming lemming;
    private Barrier barrier;
    private Barrier barrierLeft;
    private Lemming lemming2;


    public World(double width, double height) {
        this.width = width;
        this.height = height;
        lemming = new Lemming(100,50);
        barrier = new Barrier(300,0,20,200);
        barrierLeft = new Barrier(10,0,20,200);
        lemming2 = new Lemming(200,50);

    }

    public void draw(GraphicsContext gc){
        gc.save();
        // Change coordinate system to human like
        gc.scale(1, -1);
        gc.translate(0, -height);
        lemming.draw(gc);
        barrier.draw(gc);
        barrierLeft.draw(gc);
        lemming2.draw(gc);
        //...
        gc.restore();
    }

    public void simulate(double deltaTime){
        lemming.simulate(deltaTime);
        lemming.checkCollision(barrier);
        lemming.checkCollision(barrierLeft);

        lemming2.simulate(deltaTime);
        lemming2.checkCollision(barrier);
        lemming2.checkCollision(barrierLeft);
    }

}
