package lab;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LevelDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label totalLabel;
    @FXML private Label neededLabel;
    @FXML private Label entryLabel;
    @FXML private Label abilitiesLabel;
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
        playButton.setDisable(false);
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

            // Uložit reference před zavřením
            Stage detailsStage = (Stage) playButton.getScene().getWindow();
            Stage selectionStage = ownerStage;

            // Zobrazit herní okno
            gameStage.show();

            // Spustit level až po renderování scény
            final Level lvl = selectedLevel;
            Platform.runLater(() -> gc.startLevel(lvl));

            // Zavřít selection a details
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
