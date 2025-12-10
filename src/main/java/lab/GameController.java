package lab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private int currentLevel = 1;
    private long levelStartTime;
    private List<Score> scores = new ArrayList<>();

    @FXML private Slider angle;
    @FXML private Slider speed;
    @FXML private Canvas canvas;

    private World world;
    private DrawingThread timer;
    private Role selectedRole = null;

    @FXML
    void block(ActionEvent event) {
        selectedRole = Role.BLOCK;
    }

    @FXML
    void bomb(ActionEvent event) {
        selectedRole = Role.BOMB;
    }

    @FXML
    void build(ActionEvent event) {
        selectedRole = Role.BUILD;
    }

    @FXML
    void reset(ActionEvent event) {
        onLevelCompleted();
        if (timer != null) timer.stop();
        world = new World(canvas.getWidth(), canvas.getHeight());
        timer = new DrawingThread(canvas, world);
        timer.start();
        selectedRole = null;
        startLevelTimer();
    }

    @FXML
    void stop(ActionEvent event) {
        stop();
        Stage stage = (Stage) canvas.getScene().getWindow();
        stage.close();
    }

    @FXML
    void initialize() {
        // NESPOUŠTĚT timer zde — čekat na startLevel nebo explicitní akci
        try {
            scores = ScoreRepository.load();
        } catch (ScoreException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Loading problem");
            alert.getDialogPane().setContentText(ex.getMessage());
            alert.showAndWait();
            scores = new ArrayList<>();
        }

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleCanvasClick);
    }

    private void handleCanvasClick(MouseEvent e) {
        if (selectedRole == null || world == null) return;

        double mx = e.getX();
        double my = canvas.getHeight() - e.getY();

        Lemming target = null;
        for (Lemming l : world.getLemmings()) {
            if (l.getBoundingBox().contains(mx, my)) {
                target = l;
                break;
            }
        }

        if (target == null) {
            selectedRole = null;
            return;
        }

        switch (selectedRole) {
            case BLOCK -> target.setRole(target.getRole() == Role.BLOCK ? Role.DEFAULT : Role.BLOCK);
            case BOMB -> world.getLemmings().remove(target);
            case BUILD -> target.buildStairs(world, 5);
        }
        selectedRole = null;
    }

    private void startLevelTimer() {
        levelStartTime = System.currentTimeMillis();
    }

    public void startLevel(Level level) {
        if (level == null) {
            // fallback: spustit defaultní svět
            world = new World(canvas.getWidth(), canvas.getHeight());
        } else {
            currentLevel = level.getId();
            world = World.fromLevel(level, canvas.getWidth(), canvas.getHeight());
        }

        if (timer != null) timer.stop();
        timer = new DrawingThread(canvas, world);
        timer.start();
        startLevelTimer();
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
        if (existing != null) scores.remove(existing);
        scores.add(new Score(currentLevel, true, time));

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
        if (timer != null) timer.stop();
    }
}
