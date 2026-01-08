package net.dobiasovsky.michal.stargate;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LevelRepository {

    public List<Level> loadDefaults() {
        List<Level> levels = new ArrayList<>();

        // level 1 - First steps
        List<Barrier> barriers1 = new ArrayList<>();
        barriers1.add(new Barrier(18, 10, 307, 20));
        barriers1.add(new Barrier(396, 160, 591, 20));

        List<Door> doors1 = new ArrayList<>();
        doors1.add(new Door(30, 30, DoorType.ENTRY));
        doors1.add(new Door(887, 180, DoorType.EXIT));

        Map<Role, Integer> abilities1 = new EnumMap<>(Role.class);
        abilities1.put(Role.BLOCK, 2);
        abilities1.put(Role.BUILD, 1);
        abilities1.put(Role.KILL, 2);

        Level l1 = new Level(
            1,
            "First steps",
            "/net/dobiasovsky/michal/stargate/level1.png",
            10,
            5,
            new Point2D(50, 50),
            barriers1,
            doors1,
            abilities1
        );
        levels.add(l1);

        // level 2 - Deep Dive
        List<Barrier> barriers2 = new ArrayList<>();
        barriers2.add(new Barrier(59, 10, 834, 20));
        barriers2.add(new Barrier(396, 310, 494, 20));
        barriers2.add(new Barrier(874, 9, 20, 321));

        List<Door> doors2 = new ArrayList<>();
        doors2.add(new Door(473, 330, DoorType.ENTRY));
        doors2.add(new Door(639, 30, DoorType.EXIT));

        Map<Role, Integer> abilities2 = new EnumMap<>(Role.class);
        abilities2.put(Role.BLOCK, 2);
        abilities2.put(Role.BUILD, 0);
        abilities2.put(Role.KILL, 2);

        Level l2 = new Level(
            2,
            "Deep Dive",
            "/net/dobiasovsky/michal/stargate/level2.png",
            15,
            5,
            new Point2D(50, 50),
            barriers2,
            doors2,
            abilities2
        );
        levels.add(l2);

        // level 3 - Stairs
        List<Barrier> barriers3 = new ArrayList<>();
        barriers3.add(new Barrier(191, 13, 20, 163));
        barriers3.add(new Barrier(191, 160, 178, 20));
        barriers3.add(new Barrier(354, 162, 20, 166));
        barriers3.add(new Barrier(355, 310, 189, 20));
        barriers3.add(new Barrier(524, 313, 20, 166));
        barriers3.add(new Barrier(523, 460, 168, 20));
        barriers3.add(new Barrier(14, 10, 999, 20));

        List<Door> doors3 = new ArrayList<>();
        doors3.add(new Door(23, 30, DoorType.ENTRY));
        doors3.add(new Door(464, 30, DoorType.EXIT));

        Map<Role, Integer> abilities3 = new EnumMap<>(Role.class);
        abilities3.put(Role.BLOCK, 1);
        abilities3.put(Role.BUILD, 3);
        abilities3.put(Role.KILL, 1);

        Level l3 = new Level(
            3,
            "Stairs",
            "level3.png",
            5,
            3,
            new Point2D(50, 50),
            barriers3,
            doors3,
            abilities3
        );
        levels.add(l3);

        // level 4 - Broken space
        List<Barrier> barriers4 = new ArrayList<>();
        barriers4.add(new Barrier(774, 10, 211, 20));
        barriers4.add(new Barrier(17, 10, 167, 20));
        barriers4.add(new Barrier(274, 460, 378, 20));
        barriers4.add(new Barrier(15, 310, 180, 20));
        barriers4.add(new Barrier(251, 760, 606, 20));
        barriers4.add(new Barrier(9, 610, 196, 20));
        barriers4.add(new Barrier(9, 310, 20, 319));
        barriers4.add(new Barrier(660, 310, 82, 20));
        barriers4.add(new Barrier(270, 160, 191, 20));
        barriers4.add(new Barrier(442, 466, 20, 143));

        List<Door> doors4 = new ArrayList<>();
        doors4.add(new Door(891, 30, DoorType.EXIT));
        doors4.add(new Door(27, 30, DoorType.ENTRY));

        Map<Role, Integer> abilities4 = new EnumMap<>(Role.class);
        abilities4.put(Role.BLOCK, 5);
        abilities4.put(Role.BUILD, 5);
        abilities4.put(Role.KILL, 5);

        Level l4 = new Level(
            4,
            "Broken space",
            "level4.png",
            15,
            5,
            new Point2D(50, 50),
            barriers4,
            doors4,
            abilities4
        );
        levels.add(l4);

        // level 5 - Last Step
        List<Barrier> barriers5 = new ArrayList<>();
        barriers5.add(new Barrier(848, 310, 169, 20));
        barriers5.add(new Barrier(16, 460, 177, 20));
        barriers5.add(new Barrier(311, 310, 136, 20));
        barriers5.add(new Barrier(540, 160, 251, 20));

        List<Door> doors5 = new ArrayList<>();
        doors5.add(new Door(84, 480, DoorType.ENTRY));
        doors5.add(new Door(897, 330, DoorType.EXIT));

        Map<Role, Integer> abilities5 = new EnumMap<>(Role.class);
        abilities5.put(Role.BLOCK, 0);
        abilities5.put(Role.BUILD, 3);
        abilities5.put(Role.KILL, 5);

        Level l5 = new Level(
            5,
            "Last Step",
            "level5.png",
            20,
            15,
            new Point2D(50, 50),
            barriers5,
            doors5,
            abilities5
        );
        levels.add(l5);

        return levels;
    }
}
