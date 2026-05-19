package net.dobiasovsky.lastmission;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import net.dobiasovsky.lastmission.Role;
import net.dobiasovsky.lastmission.score.ScoreException;
import net.dobiasovsky.lastmission.score.Score;
import net.dobiasovsky.lastmission.score.ScoreRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.ResourceBundle;

@Log4j2
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
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
    @Setter
    private App app;

    private ResourceBundle bundle() {
        return ResourceBundle.getBundle("msg", Locale.getDefault());
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
                    text += " (" + bundle().getString("label.completed") + ")";
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
        nameLabel.setText(bundle().getString("label.name") + ": -");
        setIfPresent(totalLabel, bundle().getString("label.total") + ": -");
        setIfPresent(neededLabel, bundle().getString("label.needed") + ": -");
        setIfPresent(abilitiesLabel, bundle().getString("label.abilities") + ": -");
        setIfPresent(bestTimeLabel, bundle().getString("label.best_time") + ": -");
    }

    private void renderBaseDetails(Level lvl) {
        nameLabel.setText(bundle().getString("label.name") + ": " + textOrDash(lvl.getName()));
        setIfPresent(totalLabel, bundle().getString("label.total") + ": " + lvl.getTotalLemmings());
        setIfPresent(neededLabel, bundle().getString("label.needed") + ": " + lvl.getNeededLemmings());
    }

    private void renderAbilitiesDetails(Level lvl) {
        if (abilitiesLabel == null) {
            return;
        }

        Map<Role, Integer> abilities = lvl.getAbilityCounts();
        if (abilities == null) {
            abilitiesLabel.setText(bundle().getString("label.abilities") + ": -");
            return;
        }

        StringBuilder sb = new StringBuilder(bundle().getString("label.abilities")).append(": ");
        sb.append(bundle().getString("ability.block")).append(" ").append(abilities.getOrDefault(Role.BLOCK, 0));
        sb.append(", ").append(bundle().getString("ability.build")).append(" ").append(abilities.getOrDefault(Role.BUILD, 0));
        sb.append(", ").append(bundle().getString("ability.kill")).append(" ").append(abilities.getOrDefault(Role.KILL, 0));

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
            bestTimeLabel.setText(bundle().getString("label.best_time") + ": -");
            return;
        }

        bestTimeLabel.setText(buildTop10Text(top));
    }

    private List<Score> safeScores() {
        return scores == null ? List.of() : scores;
    }

    private String buildTop10Text(List<Score> top) {
        StringBuilder sb = new StringBuilder();
        sb.append(bundle().getString("label.top_10")).append(":").append(System.lineSeparator());
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
        return (s == null || s.isBlank()) ? bundle().getString("label.anonymous") : s;
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
