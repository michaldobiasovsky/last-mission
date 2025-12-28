// java
// `src/main/java/lab/World.java`
package lab;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class World {

    private final List<Lemming> lemmings = new ArrayList<>();
    private final List<Barrier> barriers = new ArrayList<>();
    private final List<Door> doors = new ArrayList<>();
    private final List<SpawnRequest> spawnRequests = new ArrayList<>();

    private final double canvasWidth;
    private final double canvasHeight;

    private int exitedCount = 0;

    public World(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public static World fromLevel(Level level, double canvasWidth, double canvasHeight) {
        World w = new World(canvasWidth, canvasHeight);
        if (level == null) {
            return w;
        }

        w.getBarriers().addAll(level.getBarriers());
        w.getDoors().addAll(level.getDoors());

        if (level.getTotalLemmings() > 0) {
            w.spawnFromEntry(level.getTotalLemmings(), 1.0);
        }
        return w;
    }

    public List<Lemming> getLemmings() { return lemmings; }
    public List<Barrier> getBarriers() { return barriers; }
    public List<Door> getDoors() { return doors; }
    public int getExitedCount() { return exitedCount; }

    public void spawnFromEntry(int count, double intervalSeconds) {
        Door entry = doors.stream().filter(d -> d.getType() == DoorType.ENTRY).findFirst().orElse(null);
        if (entry == null || count <= 0 || intervalSeconds <= 0) return;
        spawnRequests.add(new SpawnRequest(entry, count, intervalSeconds));
    }

    private static class SpawnRequest {
        final Door door;
        int remaining;
        final double interval;
        double acc = 0.0;

        SpawnRequest(Door door, int remaining, double interval) {
            this.door = door;
            this.remaining = remaining;
            this.interval = interval;
        }
    }

    public List<Drawable> getDrawables() {
        List<Drawable> all = new ArrayList<>();
        all.addAll(lemmings);
        all.addAll(barriers);
        all.addAll(doors);
        all.sort(Comparator.comparingDouble(d -> ((HasBoundingBox) d).getY()));
        return all;
    }

    public List<HasBoundingBox> getCollidables() {
        List<HasBoundingBox> all = new ArrayList<>();
        all.addAll(lemmings);
        all.addAll(barriers);
        all.addAll(doors);
        return all;
    }

    public void draw(GraphicsContext gc) {
        gc.save();
        gc.translate(0, canvasHeight);
        gc.scale(1, -1);

        for (Barrier b : barriers) b.draw(gc);
        for (Door d : doors) d.draw(gc);
        for (Lemming l : lemmings) l.draw(gc);

        gc.restore();
    }

    public void simulate(double deltaTime) {
        processSpawnRequests(deltaTime);

        for (Lemming l : lemmings) {
            l.simulate(deltaTime, this);
        }

        // \- lemmingy, kteří úspěšně odešli exitem
        List<Lemming> exited = new ArrayList<>();
        for (Lemming l : lemmings) {
            for (Door d : doors) {
                if (d.getType() == DoorType.EXIT && d.isLemmingExiting(l)) {
                    exited.add(l);
                    break;
                }
            }
        }
        if (!exited.isEmpty()) {
            exitedCount += exited.size();
            lemmings.removeAll(exited);
        }

        List<Lemming> outOfBounds = new ArrayList<>();
        for (Lemming l : lemmings) {
            double x = l.getX();
            double y = l.getY();
            if (x < -Lemming.WIDTH || x > canvasWidth || y < -Lemming.HEIGHT || y > canvasHeight) {
                outOfBounds.add(l);
            }
        }
        if (!outOfBounds.isEmpty()) {
            lemmings.removeAll(outOfBounds);
        }
    }

    private void processSpawnRequests(double deltaTime) {
        if (spawnRequests.isEmpty()) return;

        List<SpawnRequest> finished = new ArrayList<>();
        for (SpawnRequest req : spawnRequests) {
            req.acc += deltaTime;
            while (req.remaining > 0 && req.acc >= req.interval) {
                req.acc -= req.interval;
                double spawnX = req.door.getX() + (req.door.getWidth() - Lemming.WIDTH) / 2.0;
                double spawnY = req.door.getY() + (req.door.getHeight() - Lemming.HEIGHT) / 2.0;
                lemmings.add(new Lemming(spawnX, spawnY));
                req.remaining--;
            }
            if (req.remaining <= 0) finished.add(req);
        }
        spawnRequests.removeAll(finished);
    }
}
