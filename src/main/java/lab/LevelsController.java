// java
// src/main/java/lab/LevelsController.java
package lab;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LevelsController {

    @FXML
    private GridPane grid;

    @FXML
    public void initialize() {
        List<Level> levels = LevelRepository.loadDefaults();
        int cols = 4;
        int row = 0;
        int col = 0;
        Stage selectionStage = null;
        for (Level lvl : levels) {
            Button b = new Button(String.valueOf(lvl.getId()));
            b.setPrefSize(60, 60);
            b.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab/levelDetails.fxml"));
                    Parent root = loader.load();
                    LevelDetailsController ctrl = loader.getController();
                    // předání levelu a owner stage (selection)
                    Stage detailsStage = new Stage();
                    Stage owner = (Stage) grid.getScene().getWindow();
                    ctrl.setOwnerStage(owner);
                    ctrl.setLevel(lvl);
                    detailsStage.initOwner(owner);
                    detailsStage.initModality(Modality.WINDOW_MODAL);
                    detailsStage.setTitle("Level " + lvl.getId());
                    detailsStage.setScene(new javafx.scene.Scene(root));
                    detailsStage.setResizable(false);
                    detailsStage.show();
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
