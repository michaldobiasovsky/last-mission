package net.dobiasovsky.michal.stargate;

import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class World {

    @Getter
    private final List<Lemming> lemmings = new ArrayList<>();
    @Getter
    private final List<Barrier> barriers = new ArrayList<>();
    private final List<Door> doors = new ArrayList<>();
    private final List<SpawnRequest> spawnRequests = new ArrayList<>();

    private final double canvasWidth;
    private final double canvasHeight;

    @Getter
    private int exitedCount = 0;

    public World(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public World(Level level, double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        if (level != null) {
            this.barriers.addAll(level.getBarriers());
            this.doors.addAll(level.getDoors());

            if (level.getTotalLemmings() > 0) {
                this.spawnFromEntry(level.getTotalLemmings(), 1.7);
            }
        }
    }


    public void spawnFromEntry(int count, double intervalSeconds) {
        Door entry = doors.stream().filter(d -> d.getType() == DoorType.ENTRY).findFirst().orElse(null);
        if (entry == null || count <= 0 || intervalSeconds <= 0) return;
        spawnRequests.add(new SpawnRequest(entry, count, intervalSeconds));
    }

    public boolean isOutOfLemmings() {
        if (!lemmings.isEmpty()) return false;
        for (SpawnRequest req : spawnRequests) {
            if (req.remaining > 0) return false;
        }
        return true;
    }

    private class SpawnRequest {
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
            if (x < -l.getWidth() || x > canvasWidth || y < -l.getHeight() || y > canvasHeight) {
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

                Lemming dummy = new Lemming(0, 0);
                double lWidth = dummy.getWidth();
                double lHeight = dummy.getHeight();

                double spawnX = req.door.getX() + (req.door.getWidth() - lWidth) / 2.0;
                double spawnY = req.door.getY() + (req.door.getHeight() - lHeight) / 2.0;

                lemmings.add(new Lemming(spawnX, spawnY));
                req.remaining--;
            }
            if (req.remaining <= 0) finished.add(req);
        }
        spawnRequests.removeAll(finished);
    }
}
