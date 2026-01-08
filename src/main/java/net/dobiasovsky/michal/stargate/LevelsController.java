package net.dobiasovsky.michal.stargate;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import net.dobiasovsky.michal.stargate.score.ScoreException;
import net.dobiasovsky.michal.stargate.score.Score;
import net.dobiasovsky.michal.stargate.score.ScoreRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LevelsController {

    @FXML private ListView<Level> levelList;
    @FXML private Label nameLabel;
    @FXML private Label totalLabel;
    @FXML private Label neededLabel;
    @FXML private Label abilitiesLabel;
    @FXML private Label bestTimeLabel;
    @FXML private Button playButton;

    private Level selected;
    private List<Score> scores;
    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        List<Level> levels = new LevelRepository().loadDefaults();
        try {
            ScoreRepository repository = new ScoreRepository();
            scores = repository.load();
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
                    return;
                }

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
        });

        if (playButton != null) {
            playButton.setDisable(true);
        }

        levelList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selected = newV;
            renderDetails(newV);
            if (playButton != null) {
                playButton.setDisable(newV == null);
            }
        });

        if (!levels.isEmpty()) {
            levelList.getSelectionModel().selectFirst();
        }
    }

    private void renderDetails(Level lvl) {
        if (nameLabel == null) {
            return;
        }

        if (lvl == null) {
            renderEmptyDetails();
            return;
        }

        renderBaseDetails(lvl);
        renderAbilitiesDetails(lvl);
        renderBestTimeDetails(lvl);
    }

    private void renderEmptyDetails() {
        nameLabel.setText("Name: -");
        setIfPresent(totalLabel, "Total: -");
        setIfPresent(neededLabel, "Needed: -");
        setIfPresent(abilitiesLabel, "Abilities: -");
        setIfPresent(bestTimeLabel, "Best time: -");
    }

    private void renderBaseDetails(Level lvl) {
        nameLabel.setText("Name: " + textOrDash(lvl.getName()));
        setIfPresent(totalLabel, "Total: " + lvl.getTotalLemmings());
        setIfPresent(neededLabel, "Needed: " + lvl.getNeededLemmings());
    }

    private void renderAbilitiesDetails(Level lvl) {
        if (abilitiesLabel == null) {
            return;
        }

        Map<Role, Integer> abilities = lvl.getAbilityCounts();
        if (abilities == null) {
            abilitiesLabel.setText("Abilities: -");
            return;
        }

        StringBuilder sb = new StringBuilder("Abilities: ");
        sb.append("Block ").append(abilities.getOrDefault(Role.BLOCK, 0));
        sb.append(", Build ").append(abilities.getOrDefault(Role.BUILD, 0));
        sb.append(", Kill ").append(abilities.getOrDefault(Role.KILL, 0));

        abilitiesLabel.setText(sb.toString());
    }

    private void renderBestTimeDetails(Level lvl) {
        if (bestTimeLabel == null) {
            return;
        }

        List<Score> top = safeScores().stream()
            .filter(s -> s.getLevel() == lvl.getId() && s.isUnlocked())
            .sorted(Comparator.comparingLong(Score::getTime))
            .limit(10)
            .toList();

        if (top.isEmpty()) {
            bestTimeLabel.setText("Best time: -");
            return;
        }

        bestTimeLabel.setText(buildTop10Text(top));
    }

    private List<Score> safeScores() {
        return scores == null ? List.of() : scores;
    }

    private String buildTop10Text(List<Score> top) {
        StringBuilder sb = new StringBuilder();
        sb.append("Top 10:").append(System.lineSeparator());
        int idx = 1;
        for (Score s : top) {
            sb.append(String.format(
                "%d) %s - %s%n",
                idx++,
                textOrAnonymous(s.getPlayerName()),
                formatTime(s.getTime())
            ));
        }
        return sb.toString().trim();
    }

    private String textOrDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private String textOrAnonymous(String s) {
        return (s == null || s.isBlank()) ? "Anonymous" : s;
    }

    private void setIfPresent(Label label, String text) {
        if (label != null) {
            label.setText(text);
        }
    }

    private boolean isLevelWon(int levelId) {
        if (scores == null) {
            return false;
        }
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
        if (selected == null || app == null) {
            return;
        }
        try {
            app.switchToGame(selected);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBack() {
        if (app == null) {
            return;
        }
        try {
            app.switchToMainMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
