package lab;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class World {

    private double width;
    private double height;
    private Bullet bullet;
    private BulletAnimated bullet2;
    private Cannon cannon;

    public World(double width, double height) {
        this.width = width;
        this.height = height;
        bullet = new Bullet();
        bullet2= new BulletAnimated(new Point2D(10,300), new Point2D(100,0), new Point2D(0,-9.1));
        cannon = new Cannon(20, new Point2D(0,0));
    }

    public void draw(GraphicsContext gc){
        gc.save();
        // Change coordinate system to human like
        gc.scale(1, -1);
        gc.translate(0, -height);
        bullet.draw(gc);
        bullet2.draw(gc);
        cannon.draw(gc);
        //...
        gc.restore();
    }

    public void simulate(double deltaTime){
        bullet.simulate(deltaTime);
        bullet2.simulate(deltaTime);
        cannon.simulate(deltaTime);
    }

}
