package lab;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LevelDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label totalLabel;
    @FXML private Label neededLabel;
    @FXML private Label entryLabel;
    @FXML private Label abilitiesLabel;
    @FXML private Label bestTimeLabel;
    @FXML private Button playButton;

    private Level selectedLevel;
    private Stage ownerStage;

    public void setOwnerStage(Stage owner) {
        this.ownerStage = owner;
    }

    public void setLevel(Level lvl) {
        this.selectedLevel = lvl;
        if (lvl == null) return;
        nameLabel.setText("Name: " + (lvl.getName() != null ? lvl.getName() : "-"));
        totalLabel.setText("Total: " + lvl.getTotalLemmings());
        neededLabel.setText("Needed: " + lvl.getNeededLemmings());
        if (lvl.getEntryPosition() != null) {
            entryLabel.setText("Entry: " + String.format("%.0f, %.0f",
                lvl.getEntryPosition().getX(), lvl.getEntryPosition().getY()));
        } else {
            entryLabel.setText("Entry: -");
        }
        StringBuilder sb = new StringBuilder();
        if (lvl.getAbilityCounts() != null) {
            lvl.getAbilityCounts().forEach((r, c) -> sb.append(r).append(": ").append(c).append("  "));
        }
        abilitiesLabel.setText("Abilities: " + sb);

        bestTimeLabel.setText("Best time: -");
        try {
            List<Score> scores = ScoreRepository.load();
            Optional<Score> best = scores.stream()
                .filter(s -> s.getLevel() == lvl.getId() && s.isUnlocked())
                .min(Comparator.comparingLong(Score::getTimeMillis));
            best.ifPresent(s -> bestTimeLabel.setText("Best time: " + formatTime(s.getTimeMillis())));
        } catch (ScoreException e) {
        }

        playButton.setDisable(false);
    }

    private String formatTime(long ms) {
        long sec = ms / 1000;
        long min = sec / 60;
        long rem = sec % 60;
        return String.format("%02d:%02d", min, rem);
    }

    @FXML
    private void onBack() {
        ((Stage) playButton.getScene().getWindow()).close();
    }

    @FXML
    private void onPlay() {
        if (selectedLevel == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab/gameWindow.fxml"));
            Parent gameRoot = loader.load();
            GameController gc = loader.getController();

            Stage gameStage = new Stage();
            gameStage.setTitle("Lemmings - Level " + selectedLevel.getId());
            gameStage.setScene(new Scene(gameRoot));
            gameStage.setResizable(false);
            gameStage.getIcons().add(new Image(getClass().getResourceAsStream("/lab/stay.gif")));


            Stage detailsStage = (Stage) playButton.getScene().getWindow();
            Stage selectionStage = ownerStage;

            gameStage.show();

            final Level lvl = selectedLevel;
            Platform.runLater(() -> gc.startLevel(lvl));

            if (selectionStage != null) selectionStage.close();
            detailsStage.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Chyba");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
}
