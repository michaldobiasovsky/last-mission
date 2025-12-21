package lab;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class World {

    private final List<Lemming> lemmings = new ArrayList<>();
    private final List<Barrier> barriers = new ArrayList<>();
    private final List<Door> doors = new ArrayList<>();

    private final List<SpawnRequest> spawnRequests = new ArrayList<>();

    private int exitedCount = 0;

    public World(double width, double height) {
        barriers.add(new Barrier(10, 0, 20, 200)); // levá bariéra
        barriers.add(new Barrier(0, 5, width / 2, 20)); // podlaha

        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            double x = 40 + rnd.nextDouble() * (width - 80);
            double y = 40 + rnd.nextDouble() * (height - 80);
        }

        doors.add(new Door(300, 20, DoorType.EXIT));
        doors.add(new Door(20, 20, DoorType.ENTRY));
    }

    private World() {
    }

    public static World fromLevel(Level level, double canvasWidth, double canvasHeight) {
        World w = new World();
        if (level == null) return w;

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
        gc.scale(1, -1);
        gc.translate(0, -gc.getCanvas().getHeight());
        for (Drawable d : getDrawables()) {
            d.draw(gc);
        }
        gc.restore();
    }

    public void simulate(double deltaTime) {
        processSpawnRequests(deltaTime);

        for (Lemming l : lemmings) {
            l.simulate(deltaTime, this);
        }

        List<Lemming> toRemove = new ArrayList<>();
        for (Lemming l : getLemmings()) {
            for (Door d : getDoors()) {
                if (d.getType() == DoorType.EXIT && l.getBoundingBox().intersects(d.getBoundingBox())) {
                    toRemove.add(l);
                    break;
                }
            }
        }
        if (!toRemove.isEmpty()) {
            exitedCount += toRemove.size(); // \- inkrementace počitadla při průchodu EXITem
            getLemmings().removeAll(toRemove);
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
            if (req.remaining <= 0) {
                finished.add(req);
            }
        }
        spawnRequests.removeAll(finished);
    }
}
