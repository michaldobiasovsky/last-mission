package net.dobiasovsky.michal.stargate;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lombok.Getter;

public class Door extends Entity {
    private static final double SCALE = 0.7;

    private final double width;
    private final double height;
    @Getter
    private final DoorType type;
    private final Image entryImg;
    private final Image exitImg;

    public Door(double x, double y, DoorType type) {
        super(x, y);
        this.entryImg = new Image(Door.class.getResourceAsStream("stargate.gif"));
        this.exitImg = new Image(Door.class.getResourceAsStream("rocket.gif"));

        Image base = (type == DoorType.EXIT) ? exitImg : entryImg;
        this.width = base.getWidth() * SCALE;
        this.height = base.getHeight() * SCALE;
        this.type = type;
    }

    @Override public double getWidth() { return width; }
    @Override public double getHeight() { return height; }

    @Override
    public void draw(GraphicsContext gc) {
        Image img = (type == DoorType.EXIT) ? exitImg : entryImg;
        gc.save();
        gc.scale(1, -1);
        gc.drawImage(img, getX(), -getY() - height, width, height);
        gc.restore();
    }

    public boolean isLemmingExiting(Lemming l) {
        if (type != DoorType.EXIT) return false;

        double lx = l.getX();
        double ly = l.getY();
        double lw = l.getWidth();
        double lh = l.getHeight();

        boolean overlapX = lx < getX() + width && lx + lw > getX();
        boolean overlapY = ly < getY() + height && ly + lh > getY();
        if (!overlapX || !overlapY) return false;

        double lemmingCenterX = lx + lw / 2.0;
        double doorCenterX = getX() + width / 2.0;
        double tolerance = width * 0.15;

        return Math.abs(lemmingCenterX - doorCenterX) <= tolerance;
    }
}
