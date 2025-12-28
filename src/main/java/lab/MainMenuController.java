package lab;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainMenuController {

    @FXML private Button startButton;
    @FXML private Button exitButton;
    @FXML private Button levelsButton;
    @FXML private Button loginButton;
    @FXML private Button resetScoreButton;

    @FXML
    void startGame(ActionEvent event) {
        Level levelToStart = chooseLevelToStart();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab/gameWindow.fxml"));
            Parent gameRoot = loader.load();
            GameController gc = loader.getController();

            Stage gameStage = new Stage();
            gameStage.setTitle("Lemmings");
            gameStage.setScene(new Scene(gameRoot));
            gameStage.setResizable(false);
            gameStage.show();

            final Level lvl = levelToStart;
            Platform.runLater(() -> gc.startLevel(lvl));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void showLevels(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab/levelsSelection.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Levels");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/lab/stay.gif")));
            stage.showAndWait();
            updateStartButtonText();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void login(ActionEvent event) {
        Protection.checkAndApply(success -> {
            if (Protection.isVerified()) {
                loginButton.setText("Přihlášen: " + Protection.getVerifiedName().orElse(""));
            } else {
                loginButton.setText("Přihlásit");
            }
        });
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
    }

    @FXML
    void initialize() {
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'mainMenu.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'mainMenu.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'mainMenu.fxml'.";

        if (Protection.isVerified()) {
            loginButton.setText("Přihlášen: " + Protection.getVerifiedName().orElse(""));
            startButton.setDisable(false);
        } else {
            loginButton.setText("Přihlásit");
            startButton.setDisable(false);
        }
        updateStartButtonText();
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
