package lab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private int currentLevel = 1;
    private long levelStartTime;
    private List<Score> scores = new ArrayList<>();

    @FXML
    private Slider angle;

    @FXML
    private Slider speed;

    @FXML
    private Canvas canvas;

    private World world;
    private DrawingThread timer;

    private Role selectedRole = null;

    @FXML
    void block(ActionEvent event) {
        selectedRole = Role.BLOCK;
        System.out.println("Selected role: BLOCK");
    }

    @FXML
    void bomb(ActionEvent event) {
        selectedRole = Role.BOMB;
        System.out.println("Selected role: BOMB");
    }

    @FXML
    void build(ActionEvent event) {
        selectedRole = Role.BUILD;
        System.out.println("Selected role: BUILD");
    }

    @FXML
    void reset(ActionEvent event) {
        onLevelCompleted();

        System.out.println("Resetting world to initial state...");
        if (timer != null) {
            timer.stop();
        }
        world = new World(canvas.getWidth(), canvas.getHeight());
        timer = new DrawingThread(canvas, world);
        timer.start();
        selectedRole = null;

        startLevelTimer();
    }

    @FXML
    void initialize() {
        assert canvas != null : "fx:id=\"canvas\" was not injected: check your FXML file `gameWindow.fxml`.";

        world = new World(canvas.getWidth(), canvas.getHeight());
        timer = new DrawingThread(canvas, world);
        timer.start();

        try {
            scores = ScoreRepository.load();
        } catch (ScoreException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Loading problem");
            alert.getDialogPane().setContentText(ex.getMessage());
            alert.showAndWait();
            scores = new ArrayList<>();
        }

        startLevelTimer();

        Protection.checkAndApply();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (selectedRole == null) {
                return;
            }

            double mx = e.getX();
            double my = canvas.getHeight() - e.getY();

            Lemming target = null;
            for (Lemming l : world.getLemmings()) {
                Rectangle2D bb = l.getBoundingBox();
                if (bb.contains(mx, my)) {
                    target = l;
                    break;
                }
            }

            if (target == null) {
                selectedRole = null;
                return;
            }

            switch (selectedRole) {
                case BLOCK:
                    if (target.getRole() == Role.BLOCK) {
                        target.setRole(Role.DEFAULT);
                    } else {
                        target.setRole(Role.BLOCK);
                    }
                    break;
                case BOMB:
                    world.getLemmings().remove(target);
                    break;
                case BUILD:
                    if (world != null) {
                        target.buildStairs(world, 5);
                    }
                    break;
                default:
                    break;
            }
            selectedRole = null;
        });
    }

    private void startLevelTimer() {
        levelStartTime = System.currentTimeMillis();
    }


    public void onLevelCompleted() {
        long time = System.currentTimeMillis() - levelStartTime;

        Score existing = null;
        for (Score s : scores) {
            if (s.getLevel() == currentLevel) {
                existing = s;
                break;
            }
        }
        Score updated = new Score(currentLevel, true, time);

        if (existing != null) {
            scores.remove(existing);
        }
        scores.add(updated);

        try {
            ScoreRepository.save(scores);
        } catch (ScoreException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Storing problem");
            alert.getDialogPane().setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }
}
