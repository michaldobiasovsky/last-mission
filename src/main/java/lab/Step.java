package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

public class Step extends Barrier {

    private final int stepDirection;
    private final Image stairsTexImg;

    public Step(double x, double y, double lemmingWidth, double lemmingHeight, int direction) {
        super(x, y, lemmingWidth + 10, lemmingHeight);
        this.stepDirection = direction;
        this.stairsTexImg = loadStairsTexture();
    }

    private Image loadStairsTexture() {
        try (InputStream in = Objects.requireNonNull(
            Step.class.getResourceAsStream("/lab/textureStairs.jpg"),
            "Resource not found: /lab/textureStairs.jpg"
        )) {
            return new Image(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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

        double shorterSide = Math.min(w, h);
        double tile = Math.clamp(w, 1.0, shorterSide);

        ImagePattern pattern = new ImagePattern(stairsTexImg, getX(), getY(), tile, tile, false);

        gc.setFill(pattern);
        gc.fillRect(getX(), getY(), w, h);
    }
}
