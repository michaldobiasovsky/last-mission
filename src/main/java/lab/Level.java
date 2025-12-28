package lab;

import javafx.geometry.Point2D;
import java.util.List;
import java.util.Map;

public final class Level {
    private final int id;
    private final String name;
    private final String backgroundImagePath;
    private final int totalLemmings;
    private final int neededLemmings;
    private final Point2D entryPosition;
    private final List<Barrier> barriers;
    private final List<Door> doors;
    private final Map<Role, Integer> abilityCounts;

    public Level(int id, String name, String backgroundImagePath, int totalLemmings, int neededLemmings,
                 Point2D entryPosition, List<Barrier> barriers, List<Door> doors,
                 Map<Role, Integer> abilityCounts) {
        this.id = id;
        this.name = name;
        this.backgroundImagePath = backgroundImagePath;
        this.totalLemmings = totalLemmings;
        this.neededLemmings = neededLemmings;
        this.entryPosition = entryPosition;
        this.barriers = barriers;
        this.doors = doors;
        this.abilityCounts = abilityCounts;
    }

    public int getId() { return id; }
    public String getName() {
        return name;
    }
    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }
    public int getTotalLemmings() { return totalLemmings; }
    public int getNeededLemmings() { return neededLemmings; }
    public Point2D getEntryPosition() { return entryPosition; }
    public List<Barrier> getBarriers() { return barriers; }
    public List<Door> getDoors() { return doors; }
    public Map<Role, Integer> getAbilityCounts() { return abilityCounts; }
}
