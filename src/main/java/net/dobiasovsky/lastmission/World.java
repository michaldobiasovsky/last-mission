package net.dobiasovsky.lastmission;

import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class World {

    private final List<Lemming> lemmings = new ArrayList<>();
    private final List<Barrier> barriers = new ArrayList<>();
    private final List<Door> doors = new ArrayList<>();
    private final List<SpawnRequest> spawnRequests = new ArrayList<>();

    private final double canvasWidth;
    private final double canvasHeight;

    @Getter
    private volatile int exitedCount = 0;

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

    public synchronized List<Lemming> getLemmings() {
        return new ArrayList<>(lemmings);
    }

    public synchronized List<Barrier> getBarriers() {
        return new ArrayList<>(barriers);
    }

    public synchronized List<Door> getDoors() {
        return new ArrayList<>(doors);
    }

    public synchronized void addBarrier(Barrier barrier) {
        if (barrier != null) {
            barriers.add(barrier);
        }
    }

    public synchronized void addLemming(Lemming lemming) {
        if (lemming != null) {
            lemmings.add(lemming);
        }
    }

    public synchronized boolean removeLemming(Lemming lemming) {
        if (lemming == null) {
            return false;
        }
        boolean removed = lemmings.remove(lemming);
        if (removed) {
            lemming.stopBehavior();
        }
        return removed;
    }

    public Lemming findLemmingAt(double x, double y) {
        List<Lemming> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(lemmings);
        }
        for (Lemming lemming : snapshot) {
            if (lemming.getBoundingBox().contains(x, y)) {
                return lemming;
            }
        }
        return null;
    }

    public synchronized boolean containsLemming(Lemming lemming) {
        return lemmings.contains(lemming);
    }

    public synchronized List<Lemming> snapshotLemmings() {
        return new ArrayList<>(lemmings);
    }

    public synchronized List<Barrier> snapshotBarriers() {
        return new ArrayList<>(barriers);
    }

    public synchronized List<Door> snapshotDoors() {
        return new ArrayList<>(doors);
    }

    public synchronized void shutdown() {
        for (Lemming lemming : new ArrayList<>(lemmings)) {
            lemming.stopBehavior();
        }
        lemmings.clear();
        spawnRequests.clear();
    }


    public void spawnFromEntry(int count, double intervalSeconds) {
        synchronized (this) {
            Door entry = doors.stream().filter(d -> d.getType() == DoorType.ENTRY).findFirst().orElse(null);
            if (entry == null || count <= 0 || intervalSeconds <= 0) return;
            spawnRequests.add(new SpawnRequest(entry, count, intervalSeconds));
        }
    }

    public boolean isOutOfLemmings() {
        synchronized (this) {
            if (!lemmings.isEmpty()) return false;
            for (SpawnRequest req : spawnRequests) {
                if (req.remaining > 0) return false;
            }
            return true;
        }
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
        List<Barrier> barrierSnapshot;
        List<Door> doorSnapshot;
        List<Lemming> lemmingSnapshot;

        synchronized (this) {
            barrierSnapshot = new ArrayList<>(barriers);
            doorSnapshot = new ArrayList<>(doors);
            lemmingSnapshot = new ArrayList<>(lemmings);
        }

        gc.save();
        gc.translate(0, canvasHeight);
        gc.scale(1, -1);

        for (Barrier b : barrierSnapshot) b.draw(gc);
        for (Door d : doorSnapshot) d.draw(gc);
        for (Lemming l : lemmingSnapshot) l.draw(gc);

        gc.restore();
    }

    public void simulate(double deltaTime) {
        processSpawnRequests(deltaTime);

        List<Lemming> exited = new ArrayList<>();
        List<Lemming> lemmingSnapshot = snapshotLemmings();
        List<Door> doorSnapshot = snapshotDoors();
        for (Lemming l : lemmingSnapshot) {
            for (Door d : doorSnapshot) {
                if (d.getType() == DoorType.EXIT && d.isLemmingExiting(l)) {
                    exited.add(l);
                    break;
                }
            }
        }
        if (!exited.isEmpty()) {
            synchronized (this) {
                exitedCount += exited.size();
            }
            for (Lemming lemming : exited) {
                removeLemming(lemming);
            }
        }

        List<Lemming> outOfBounds = new ArrayList<>();
        for (Lemming l : lemmingSnapshot) {
            double x = l.getX();
            double y = l.getY();
            if (x < -l.getWidth() || x > canvasWidth || y < -l.getHeight() || y > canvasHeight) {
                outOfBounds.add(l);
            }
        }
        if (!outOfBounds.isEmpty()) {
            for (Lemming lemming : outOfBounds) {
                removeLemming(lemming);
            }
        }
    }

    private void processSpawnRequests(double deltaTime) {
        synchronized (this) {
            if (spawnRequests.isEmpty()) return;
        }

        List<SpawnRequest> finished = new ArrayList<>();
        synchronized (this) {
            for (SpawnRequest req : spawnRequests) {
                req.acc += deltaTime;
                while (req.remaining > 0 && req.acc >= req.interval) {
                    req.acc -= req.interval;

                    double lWidth = Lemming.getDefaultSpawnWidth();
                    double lHeight = Lemming.getDefaultSpawnHeight();

                    double spawnX = req.door.getX() + (req.door.getWidth() - lWidth) / 2.0;
                    double spawnY = req.door.getY() + (req.door.getHeight() - lHeight) / 2.0;

                    Lemming lemming = new Lemming(spawnX, spawnY);
                    lemmings.add(lemming);
                    lemming.startBehavior(this);
                    req.remaining--;
                }
                if (req.remaining <= 0) finished.add(req);
            }
            spawnRequests.removeAll(finished);
        }
    }
}
