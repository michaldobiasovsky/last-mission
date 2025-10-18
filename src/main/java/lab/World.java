package lab;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {

    private final List<Lemming> lemmings = new ArrayList<>();
    private final List<Barrier> barriers = new ArrayList<>();
    private final List<Coin> coins = new ArrayList<>();

    public World(double width, double height) {
        lemmings.add(new Lemming(50, 50));
        lemmings.add(new Lemming(200, 50));
        lemmings.add(new Lemming(100, 50));

        barriers.add(new Barrier(10, 0, 20, 200)); // levá bariéra
        barriers.add(new Barrier(0, 5, width / 2, 20)); // podlaha

        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            double x = 40 + rnd.nextDouble() * (width - 80);
            double y = 40 + rnd.nextDouble() * (height - 80);
            coins.add(new Coin(x, y));
        }
    }

    public List<Lemming> getLemmings() {
        return lemmings;
    }

    public List<Barrier> getBarriers() {
        return barriers;
    }

    public List<Coin> getCoins() {
        return coins;
    }

    public List<Drawable> getDrawables() {
        List<Drawable> all = new ArrayList<>();
        all.addAll(lemmings);
        all.addAll(barriers);
        all.addAll(coins);
        return all;
    }

    public List<HasBoundingBox> getCollidables() {
        List<HasBoundingBox> all = new ArrayList<>();
        all.addAll(lemmings);
        all.addAll(barriers);
        all.addAll(coins);
        return all;
    }

    public void draw(GraphicsContext gc) {
        gc.save();
        gc.scale(1, -1);
        gc.translate(0, -gc.getCanvas().getHeight());

        for (Drawable d : getDrawables()) {
            d.draw(gc);
        }

        gc.restore();
    }

    public void simulate(double deltaTime) {
        for (Lemming l : lemmings) {
            l.simulate(deltaTime, this);
        }
    }
}
