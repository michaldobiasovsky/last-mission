package net.dobiasovsky.michal.stargate;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.dobiasovsky.michal.stargate.score.Score;
import net.dobiasovsky.michal.stargate.score.ScoreException;
import net.dobiasovsky.michal.stargate.score.ScoreRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainMenuController {

    @FXML private Button startButton;
    @FXML private Button exitButton;
    @FXML private Button levelsButton;
    @FXML private Button resetScoreButton;
    @FXML private Button muteButton;

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void startGame(ActionEvent event) {
        Level levelToStart = chooseLevelToStart();

        if (levelToStart != null) {
            try {
                app.switchToGame(levelToStart);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void showLevels(ActionEvent event) {
        try {
            app.switchToLevelsSelection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void resetScores(ActionEvent event) {
        try {
            ScoreRepository repository = new ScoreRepository();
            repository.save(new ArrayList<>());
            updateStartButtonText();
        } catch (ScoreException e) {
            e.printStackTrace();
        }
    }

    private boolean hasAnyWonLevel() {
        try {
            ScoreRepository repository = new ScoreRepository();
            List<Score> scores = repository.load();
            return scores.stream().anyMatch(Score::isUnlocked);
        } catch (ScoreException e) {
            return false;
        }
    }

    private Level chooseLevelToStart() {
        List<Level> levels = new LevelRepository().loadDefaults();
        List<Score> scores;
        try {
            ScoreRepository repository = new ScoreRepository();
            scores = repository.load();
        } catch (ScoreException e) {
            scores = new ArrayList<>();
        }

        for (Level lvl : levels) {
            boolean won = scores.stream().anyMatch(s -> s.getLevel() == lvl.getId() && s.isUnlocked());
            if (!won) {
                return lvl;
            }
        }

        return levels.stream()
            .max(Comparator.comparingInt(Level::getId))
            .orElse(levels.isEmpty() ? null : levels.get(levels.size() - 1));
    }

    @FXML
    void exitApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void initialize() {
        updateStartButtonText();
    }

    public void updateMusicButton() {
        // Tato metoda se volá z App.java po načtení hudby
        if (app != null) {
            updateMuteButtonText(app.isMusicEnabled());
        }
    }

    private void updateMuteButtonText(boolean musicOn) {
        if (muteButton != null) {
            muteButton.setText(musicOn ? "MUSIC ON" : "MUSIC OFF");
        }
    }

    @FXML
    private void toggleMusic(ActionEvent event) {
        if (app == null) return;

        boolean newState = !app.isMusicEnabled();
        app.setMusicEnabled(newState);
        updateMuteButtonText(newState);

        try {
            new MusicSettings().saveMusicSetting(newState);
        } catch (ScoreException e) {
            e.printStackTrace();
        }
    }

    private void updateStartButtonText() {
        boolean anyWon = hasAnyWonLevel();
        startButton.setText(anyWon ? "Continue" : "New game");
    }
}
