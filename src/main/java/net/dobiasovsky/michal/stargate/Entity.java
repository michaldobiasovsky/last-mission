package net.dobiasovsky.michal.stargate;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

public abstract class Entity implements Drawable, HasBoundingBox {
    protected Point2D position;

    protected Entity(double x, double y) {
        this.position = new Point2D(x, y);
    }

    @Override
    public double getX() {
        return position.getX();
    }

    @Override
    public double getY() {
        return position.getY();
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(getX(), getY(), getWidth(), getHeight());
    }

    public abstract double getWidth();
    public abstract double getHeight();

}
