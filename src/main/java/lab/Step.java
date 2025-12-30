package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class Step extends Barrier {

    private final int stepDirection;

    private static final Image STAIRS_TEX_IMG =
        new Image(Step.class.getResourceAsStream("/lab/textureStairs.jpg"));

    public Step(double x, double y, double lemmingWidth, double lemmingHeight, int direction) {
        super(x, y, lemmingWidth + 10, lemmingHeight);
        this.stepDirection = direction;
    }

    @Override
    public boolean isStep() {
        return true;
    }

    public int getStepDirection() {
        return stepDirection;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double w = getWidth();
        double h = getHeight();

        // čtvercový tile podle kratší strany objektu
        double tile = Math.max(1.0, Math.min(w, h));

        // kotva v (getX,getY) zajistí stabilní "mřížku" vůči objektu
        ImagePattern pattern = new ImagePattern(STAIRS_TEX_IMG, getX(), getY(), tile, tile, false);

        gc.setFill(pattern);
        gc.fillRect(getX(), getY(), w, h);
    }
}
