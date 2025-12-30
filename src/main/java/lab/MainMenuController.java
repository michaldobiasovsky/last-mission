package lab;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainMenuController {

    @FXML private Button startButton;
    @FXML private Button exitButton;
    @FXML private Button levelsButton;
    @FXML private Button resetScoreButton;
    @FXML private Button muteButton;

    @FXML
    void startGame(ActionEvent event) {
        Level levelToStart = chooseLevelToStart();

        if (levelToStart != null) {
            App.showGame(levelToStart);
        }
    }

    @FXML
    public void showLevels() {
        App.showLevelsSelection();
    }


    @FXML
    void resetScores(ActionEvent event) {
        try {
            ScoreRepository.save(new ArrayList<>());
            updateStartButtonText();
        } catch (ScoreException e) {
            // ignore
        }
    }

    @FXML
    void exitApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void initialize() {
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'mainMenu.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'mainMenu.fxml'.";


        boolean musicOn = true;
        try {
            var opt = MusicSettings.loadMusicSetting();
            if (opt.isPresent()) {
                musicOn = opt.get();
            }
        } catch (Exception ignored) {
            // Intentionally ignored: if loading the music setting fails, fall back to the default value.
        }

        App.setMusicEnabled(musicOn);
        updateMuteButtonText(musicOn);
    }

    private void updateMuteButtonText(boolean musicOn) {
        if (muteButton != null) {
            muteButton.setText(musicOn ? "MUSIC ON" : "MUSIC OFF");
        }
    }

    @FXML
    private void toggleMusic(ActionEvent event) {
        boolean newState = !App.isMusicEnabled();
        App.setMusicEnabled(newState);
        updateMuteButtonText(newState);
        try {
            MusicSettings.saveMusicSetting(newState);
        } catch (ScoreException e) {
            e.printStackTrace();
        }
    }

    private void updateStartButtonText() {
        boolean anyWon = hasAnyWonLevel();
        startButton.setText(anyWon ? "Continue" : "New game");
    }

    private boolean hasAnyWonLevel() {
        try {
            List<Score> scores = ScoreRepository.load();
            return scores.stream().anyMatch(Score::isUnlocked);
        } catch (ScoreException e) {
            return false;
        }
    }

    private Level chooseLevelToStart() {
        List<Level> levels = LevelRepository.loadDefaults();
        List<Score> scores;
        try {
            scores = ScoreRepository.load();
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
            .orElseGet(() -> levels.isEmpty() ? null : levels.get(levels.size() - 1));
    }
}
