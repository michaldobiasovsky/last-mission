package lab;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.geometry.Rectangle2D;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Canvas canvas;
    private AnimationTimer timer;
    private World world;

    @Override
    public void start(Stage primaryStage) {
        try {
            setupUI(primaryStage);
            world = new World(800, 400);
            setupMouseHandler();
            timer = new DrawingThread(canvas, world);
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupUI(Stage primaryStage) {
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
    }

    private void setupMouseHandler() {
        canvas.setOnMouseClicked(event -> {
            double clickX = event.getX();
            double clickY = canvas.getHeight() - event.getY();
            handleCanvasClick(clickX, clickY);
        });
    }

    private void handleCanvasClick(double clickX, double clickY) {
        for (Drawable d : world.getDrawables()) {
            if (d instanceof HasBoundingBox hb) {
                Rectangle2D bb = hb.getBoundingBox();
                if (bb.contains(clickX, clickY)) {
                    if (d instanceof Lemming l) {
                        l.setRole(l.getRole() == Role.DEFAULT ? Role.BLOCK : Role.DEFAULT);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void stop() throws Exception {
        if (timer != null) timer.stop();
        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        System.exit(0);
    }
}
