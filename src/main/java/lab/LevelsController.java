package lab;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.io.IOException;
import java.util.List;

public class LevelsController {

    @FXML
    private GridPane grid;

    @FXML
    public void initialize() {
        List<Level> levels = LevelRepository.loadDefaults();

        List<Score> scores;
        try {
            scores = ScoreRepository.load();
        } catch (ScoreException e) {
            scores = java.util.Collections.emptyList();
        }

        int cols = 4;
        int row = 0;
        int col = 0;

        for (Level lvl : levels) {
            Button b = new Button(String.valueOf(lvl.getId()));
            b.setPrefSize(60, 60);

            boolean completed = scores.stream()
                .anyMatch(s -> s.getLevel() == lvl.getId() && s.isUnlocked());
            if (completed) {
                b.setStyle("-fx-background-color: #00C853; -fx-text-fill: white;");
            } else {
                b.setStyle("-fx-background-color: #DDDDDD;");
            }

            b.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab/levelDetails.fxml"));
                    Parent detailsRoot = loader.load();
                    LevelDetailsController ctrl = loader.getController();
                    Stage owner = (Stage) grid.getScene().getWindow();
                    ctrl.setOwnerStage(owner);
                    ctrl.setLevel(lvl);

                    Stage detailsStage = new Stage();
                    detailsStage.initOwner(owner);
                    detailsStage.initModality(Modality.APPLICATION_MODAL);
                    detailsStage.setScene(new javafx.scene.Scene(detailsRoot));
                    detailsStage.setTitle("Level " + lvl.getId());
                    detailsStage.setResizable(false);
                    detailsStage.getIcons().add(new Image(getClass().getResourceAsStream("/lab/stay.gif")));
                    detailsStage.showAndWait();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            grid.add(b, col, row);
            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
        }
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) grid.getScene().getWindow();
        stage.close();
    }
}
