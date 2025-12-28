// java
// src/main/java/lab/LevelsController.java
package lab;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

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
        for (Level lvl : levels) {
            Button b = new Button(String.valueOf(lvl.getId()));
            b.setPrefSize(60, 60);
            b.setOnAction(e -> {
                try {
                    Navigation.loadScene("/lab/levelDetails.fxml", 
                        (LevelDetailsController ctrl) -> ctrl.setLevel(lvl));
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
        try {
            Navigation.loadScene("/lab/mainMenu.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
