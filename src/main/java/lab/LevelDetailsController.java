package lab;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class LevelDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label totalLabel;
    @FXML private Label neededLabel;
    @FXML private Label entryLabel;
    @FXML private Label abilitiesLabel;
    @FXML private Button playButton;

    private Level selectedLevel;

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
        playButton.setDisable(false);
    }

    @FXML
    private void onBack() {
        try {
            Navigation.loadScene("/lab/levelsSelection.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPlay() {
        if (selectedLevel == null) return;

        try {
            Navigation.loadGameScene(selectedLevel);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
