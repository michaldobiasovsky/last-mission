package lab;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class World {

    private final List<Lemming> lemmings = new ArrayList<>();
    private final List<Barrier> barriers = new ArrayList<>();

    public World(double width, double height) {
        lemmings.add(new Lemming(50, 50));
        lemmings.add(new Lemming(200, 50));
        lemmings.add(new Lemming(100, 50));

        barriers.add(new Barrier(10, 0, 20, 200)); // levá bariéra
        barriers.add(new Barrier(0, 5, width / 2, 20)); // podlaha
        // Další bariéry lze přidat zde
    }

    public List<Lemming> getLemmings() {
        return lemmings;
    }

    public List<Barrier> getBarriers() {
        return barriers;
    }

    public void draw(GraphicsContext gc) {
        gc.save();
        gc.scale(1, -1);
        gc.translate(0, -gc.getCanvas().getHeight());

        for (Lemming l : lemmings) {
            l.draw(gc);
        }
        for (Barrier b : barriers) {
            b.draw(gc);
        }

        gc.restore();
    }

    public void simulate(double deltaTime) {
        for (Lemming l : lemmings) {
            l.simulate(deltaTime, this);
        }
    }
}
