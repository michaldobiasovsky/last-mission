// src/main/java/lab/App.java
package lab;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Canvas canvas;
    private AnimationTimer timer;
    private World world; // Přidáno

    @Override
    public void start(Stage primaryStage) {
        try {
            Group root = new Group();
            canvas = new Canvas(800, 400);
            root.getChildren().add(canvas);
            Scene scene = new Scene(root, 800, 400);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.resizableProperty().set(false);
            primaryStage.setTitle("Lemmings");
            primaryStage.show();
            primaryStage.setOnCloseRequest(this::exitProgram);

            world = new World(800, 400); // Inicializace světa

            // Obsluha kliknutí myší
            canvas.setOnMouseClicked(event -> {
                double clickX = event.getX();
                double clickY = canvas.getHeight() - event.getY(); // kvůli transformaci v draw()
                for (Lemming l : world.getLemmings()) {
                    if (clickX >= l.getX() && clickX <= l.getX() + Lemming.WIDTH &&
                        clickY >= l.getY() && clickY <= l.getY() + Lemming.HEIGHT) {
                        if (l.getRole() == Role.DEFAULT) {
                            l.setRole(Role.BLOCK);
                        } else {
                            l.setRole(Role.DEFAULT);
                        }
                    }
                }
            });

            timer = new DrawingThread(canvas, world); // Předání světa do DrawingThread
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        timer.stop();
        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        System.exit(0);
    }
}
