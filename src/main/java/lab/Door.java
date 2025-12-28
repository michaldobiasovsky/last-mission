package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Door extends Entity {
    private static final double SCALE = 0.7;

    private static final Image ENTRY_IMG = new Image(Door.class.getResourceAsStream("/lab/stargate.gif"));
    private static final Image EXIT_IMG  = new Image(Door.class.getResourceAsStream("/lab/rocket.gif"));

    private final double width;
    private final double height;
    private final DoorType type;

    public Door(double x, double y, DoorType type) {
        super(x, y);
        Image base = (type == DoorType.EXIT) ? EXIT_IMG : ENTRY_IMG;
        this.width = base.getWidth() * SCALE;
        this.height = base.getHeight() * SCALE;
        this.type = type;
    }

    @Override public double getWidth() { return width; }
    @Override public double getHeight() { return height; }
    public DoorType getType() { return type; }

    @Override
    public void draw(GraphicsContext gc) {
        Image img = (type == DoorType.EXIT) ? EXIT_IMG : ENTRY_IMG;
        gc.save();
        gc.scale(1, -1);
        gc.drawImage(img, getX(), -getY() - height, width, height);
        gc.restore();
    }

    public boolean isLemmingExiting(Lemming l) {
        if (type != DoorType.EXIT) return false;

        double lx = l.getX(), ly = l.getY();
        double lw = l.getWidth(), lh = l.getHeight();

        boolean overlapX = lx < getX() + width && lx + lw > getX();
        boolean overlapY = ly < getY() + height && ly + lh > getY();
        if (!overlapX || !overlapY) return false;

        double lemmingCenterX = lx + lw / 2.0;
        double doorCenterX = getX() + width / 2.0;
        double tolerance = width * 0.15;

        return Math.abs(lemmingCenterX - doorCenterX) <= tolerance;
    }
}
