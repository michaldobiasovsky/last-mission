package lab;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LevelsController implements StageAware {

    @FXML private ListView<Level> levelList;

    @FXML private Label nameLabel;
    @FXML private Label totalLabel;
    @FXML private Label neededLabel;
    @FXML private Label entryLabel;
    @FXML private Label abilitiesLabel;
    @FXML private Label bestTimeLabel;

    @FXML private Button playButton;

    private Stage stage;
    private List<Level> levels;
    private Level selected;

    private List<Score> scores;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        levels = LevelRepository.loadDefaults();

        try {
            scores = ScoreRepository.load();
        } catch (ScoreException e) {
            scores = new ArrayList<>();
        }

        levelList.setItems(FXCollections.observableArrayList(levels));

        levelList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Level item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean isWon = isLevelWon(item.getId());
                    String text = item.getId() + " - " + item.getName();

                    if (isWon) {
                        text += " (Completed)";
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                    setText(text);
                }
            }
        });

        if (playButton != null) playButton.setDisable(true);

        levelList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selected = newV;
            renderDetails(newV);
            if (playButton != null) playButton.setDisable(newV == null);
        });

        if (!levels.isEmpty()) {
            levelList.getSelectionModel().selectFirst();
        }
    }

    private void renderDetails(Level lvl) {
        if (nameLabel == null) return;

        if (lvl == null) {
            nameLabel.setText("Name: -");
            if (totalLabel != null) totalLabel.setText("Total: -");
            if (neededLabel != null) neededLabel.setText("Needed: -");
            if (entryLabel != null) entryLabel.setText("Entry: -");
            if (abilitiesLabel != null) abilitiesLabel.setText("Abilities: -");
            if (bestTimeLabel != null) bestTimeLabel.setText("Best time: -");
            return;
        }

        nameLabel.setText("Name: " + (lvl.getName() != null ? lvl.getName() : "-"));
        if (totalLabel != null) totalLabel.setText("Total: " + lvl.getTotalLemmings());
        if (neededLabel != null) neededLabel.setText("Needed: " + lvl.getNeededLemmings());

        if (entryLabel != null) {
            if (lvl.getEntryPosition() != null) {
                entryLabel.setText("Entry: " + String.format("%.0f, %.0f",
                    lvl.getEntryPosition().getX(), lvl.getEntryPosition().getY()));
            } else {
                entryLabel.setText("Entry: -");
            }
        }

        if (abilitiesLabel != null) {
            Map<Role, Integer> m = lvl.getAbilityCounts();
            abilitiesLabel.setText("Abilities: " + (m == null ? "-" : m.toString()));
        }

        if (bestTimeLabel != null) {
            List<Score> top = scores.stream()
                .filter(s -> s.getLevel() == lvl.getId() && s.isUnlocked())
                .sorted(Comparator.comparingLong(Score::getTime))
                .limit(10)
                .collect(Collectors.toList());

            if (top.isEmpty()) {
                bestTimeLabel.setText("Best time: -");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Top 10:\n");
                int idx = 1;
                for (Score s : top) {
                    sb.append(String.format("%d) %s - %s\n", idx++,
                        (s.getPlayerName() == null || s.getPlayerName().isBlank()) ? "Anonymous" : s.getPlayerName(),
                        formatTime(s.getTime())));
                }
                bestTimeLabel.setText(sb.toString().trim());
            }
        }
    }

    private boolean isLevelWon(int levelId) {
        return scores.stream()
            .anyMatch(s -> s.getLevel() == levelId && s.isUnlocked());
    }

    private String formatTime(long ms) {
        long sec = ms / 1000;
        long min = sec / 60;
        long rem = sec % 60;
        return String.format("%02d:%02d", min, rem);
    }

    @FXML
    private void onPlay() {
        if (selected == null) return;
        App.showGame(selected);
    }

    @FXML
    private void onBack() {
        App.showMainMenu();
    }

    @FXML
    private void onClose() {
        onBack();
    }
}
