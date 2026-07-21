# Last Mission

Last Mission is a multiplatform 2D retro platformer/puzzle game built entirely in 
Java using JavaFX for graphics and JPA (Java Persistence API) for score management. 
Guided by the principles of the classic game Lemmings, your goal is to lead stranded 
astronauts safely across hazardous environments and get them to the rocket for liftoff.

---

## Game Features

- Retro Lemmings-Like Mechanics: Control individual space explorers, assign roles, and guide them through dangerous terrain.
- Custom Game Engine: Built using a dedicated standard Java thread rendering loop via JavaFX Canvas API for smooth 60 FPS performance.
- Score System with DB Persistence: Integrates Java Persistence API (JPA) with a relational database to store player high scores locally or on a server.
- Multi-language Resource Bundles: Native localization support for English and Czech languages.

---

## Architecture & Tech Stack

- Language: Java 25
- GUI Framework: JavaFX (Canvas, Scene Graph, CSS styling)
- Database / ORM: JPA (Java Persistence API) with EclipseLink/Hibernate provider
- Build System: Apache Maven (pom.xml)
- Logging: Apache Log4j2

### Key Classes Breakdown:
- App.java: Main application entry point handling window scene switches.
- GameController.java & World.java: Core game logic, state management, and collision detection.
- DrawingThread.java: Synchronized Java thread executing the active render cycle.
- Lemming.java: Main entity class containing physics, AI states, and coordinate vectors.
- JpaConnector.java & ScoreRepository.java: Persistence layers handling database handshakes.

---

## Getting Started

### Prerequisites

- Java JDK 25
- Apache Maven
- Compatible JavaFX environment

### Build and Run

You can build and execute the application easily using Maven or the provided shell script.

#### Using Maven Wrapper:

```bash
git clone https://github.com/michaldobiasovsky/last-mission.git
cd last-mission
./mvnw clean javafx:run
```

#### Run the game:

```bash
chmod +x run.sh
./run.sh
```

---

## License

This project is open-source and licensed under the [GPL-3.0 License](LICENSE).

---

**Developed by Michal Dobiasovsky.**

