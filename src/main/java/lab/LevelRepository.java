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
        List<Barrier> barriers0 = new ArrayList<>();
        barriers0.add(new Barrier(5, 10, 800, 20));
        barriers0.add(new Barrier(200, 500, 100, 20));

        List<Door> doors0 = new ArrayList<>();
        doors0.add(new Door(10, 20, DoorType.ENTRY));
        doors0.add(new Door(700, 20, DoorType.EXIT));

        Map<Role, Integer> abilities0 = new HashMap<>();
        abilities0.put(Role.BLOCK, 3);
        abilities0.put(Role.BUILD, 2);
        abilities0.put(Role.KILL, 1);

        Level l0 = new Level(
            0,
            "Tutorial",
            "/lab/level1.png", // Cesta k pozadí pro Level 1
            10,
            5,
            new Point2D(60, 500),
            barriers0,
            doors0,
            abilities0
        );
        levels.add(l0);


        // level 1 - First Step
        List<Barrier> barriers1 = new ArrayList<>();
        barriers1.add(new Barrier(546, 10, 248, 20));
        barriers1.add(new Barrier(1, 10, 182, 20));
        barriers1.add(new Barrier(231, 160, 164, 20));
        barriers1.add(new Barrier(521, 310, 120, 20));
        barriers1.add(new Barrier(49, 310, 123, 20));
        barriers1.add(new Barrier(238, 460, 269, 20));
        barriers1.add(new Barrier(350, 470, 20, 80));
        barriers1.add(new Barrier(49, 327, 20, 154));
        barriers1.add(new Barrier(774, 20, 20, 307));

        List<Door> doors1 = new ArrayList<>();
        doors1.add(new Door(8, 30, DoorType.ENTRY));
        doors1.add(new Door(729, 30, DoorType.EXIT));

        Map<Role, Integer> abilities1 = new HashMap<>();
        abilities1.put(Role.BLOCK, 1);
        abilities1.put(Role.BUILD, 4);
        abilities1.put(Role.KILL, 5);

        Level l1 = new Level(
            1,
            "First Step",
            "/lab/level1.png",
            15,
            20,
            new Point2D(50, 50),
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





        // level 4 - Level 4
        List<Barrier> barriers4 = new ArrayList<>();
        barriers4.add(new Barrier(774, 10, 211, 20));
        barriers4.add(new Barrier(17, 10, 167, 20));
        barriers4.add(new Barrier(274, 460, 378, 20));
        barriers4.add(new Barrier(15, 310, 180, 20));
        barriers4.add(new Barrier(251, 760, 606, 20));
        barriers4.add(new Barrier(9, 610, 196, 20));
        barriers4.add(new Barrier(9, 310, 20, 319));
        barriers4.add(new Barrier(711, 310, 82, 20));
        barriers4.add(new Barrier(290, 160, 191, 20));
        barriers4.add(new Barrier(442, 466, 20, 143));

        List<Door> doors4 = new ArrayList<>();
        doors4.add(new Door(891, 30, DoorType.EXIT));
        doors4.add(new Door(27, 30, DoorType.ENTRY));

        Map<Role, Integer> abilities4 = new HashMap<>();
        abilities4.put(Role.BLOCK, 5);
        abilities4.put(Role.BUILD, 5);
        abilities4.put(Role.KILL, 5);

        Level l4 = new Level(
            4,
            "Level 4",
            "/lab/level2.png",
            15,
            5,
            new Point2D(50, 50),
            barriers4,
            doors4,
            abilities4
        );
        levels.add(l4);




        return levels;
    }
}
