package lab;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LevelRepository {

    public static List<Level> loadDefaults() {
        List<Level> levels = new ArrayList<>();

        // level 1
        List<Barrier> barriers1 = new ArrayList<>();
        barriers1.add(new Barrier(5, 10, 800, 20));
        barriers1.add(new Barrier(200, 500, 100, 20));

        List<Door> doors1 = new ArrayList<>();
        doors1.add(new Door(10, 20, DoorType.ENTRY));
        doors1.add(new Door(700, 20, DoorType.EXIT));

        Map<Role, Integer> abilities1 = new HashMap<>();
        abilities1.put(Role.BLOCK, 3);
        abilities1.put(Role.BUILD, 2);
        abilities1.put(Role.KILL, 1);

        Level l1 = new Level(
            1,
            "Tutorial",
            "/lab/level1.png", // Cesta k pozadí pro Level 1
            10,
            5,
            new Point2D(60, 500),
            barriers1,
            doors1,
            abilities1
        );
        levels.add(l1);

        // level 2
        List<Barrier> barriers2 = new ArrayList<>();
        barriers2.add(new Barrier(0, 580, 800, 20));
        barriers2.add(new Barrier(300, 520, 200, 20));

        Map<Role, Integer> abilities2 = new HashMap<>();
        abilities2.put(Role.BLOCK, 1);
        abilities2.put(Role.BUILD, 4);
        abilities2.put(Role.KILL, 0);

        Level l2 = new Level(
            2,
            "Stairs",
            "/lab/level2.png", // Cesta k pozadí pro Level 2
            15,
            8,
            new Point2D(40, 500),
            barriers2,
            new ArrayList<>(),
            abilities2
        );
        levels.add(l2);

        // level 3
        List<Barrier> barriers3 = new ArrayList<>();
        barriers3.add(new Barrier(0, 100, 1025, 20)); // Podlaha
        barriers3.add(new Barrier(400, 300, 20, 200)); // Zeď

        Map<Role, Integer> abilities3 = new HashMap<>();
        abilities3.put(Role.BLOCK, 5);
        abilities3.put(Role.BUILD, 5);
        abilities3.put(Role.KILL, 5);

        Level l3 = new Level(
            3,
            "Hardcore",
            "/lab/level3.png", // Cesta k pozadí pro Level 3
            20,
            15,
            new Point2D(50, 600),
            barriers3,
            new ArrayList<>(),
            abilities3
        );
        levels.add(l3);

        return levels;
    }
}
