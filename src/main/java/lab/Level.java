package lab;

import javafx.geometry.Point2D;
import java.util.List;
import java.util.Map;

public record Level(int id, String name, String backgroundImagePath,
                    int totalLemmings, int neededLemmings,Point2D entryPosition,
                    List<Barrier> barriers, List<Door> doors,
                    Map<Role, Integer> abilityCounts){
    public int getId() {
        return id();
    }

    public String getName() {
        return name();
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath();
    }

    public int getTotalLemmings() {
        return totalLemmings();
    }

    public int getNeededLemmings() {
        return neededLemmings();
    }

    public List<Barrier> getBarriers() {
        return barriers();
    }

    public List<Door> getDoors() {
        return doors();
    }

    public Map<Role, Integer> getAbilityCounts() {
        return abilityCounts();
    }
}
