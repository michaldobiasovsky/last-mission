// java
// src/main/java/lab/MainMenuController.java
package lab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private Button startButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button levelsButton;

    @FXML
    private Button loginButton;

    @FXML
    void startGame(ActionEvent event) {
        try {
            if (!Protection.isVerified()) {
                System.out.println("Uživatel není přihlášen, pokračuje se bez zpomalení.");
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab/gameWindow.fxml"));
            Parent gameRoot = loader.load();
            Stage stage = (Stage) startButton.getScene().getWindow();
            Scene scene = new Scene(gameRoot);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Lemmings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void showLevels(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab/levelsSelection.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initOwner(startButton.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Select Level");
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    void login(ActionEvent event) {
        Protection.checkAndApply(success -> {
            if (success) {
                loginButton.setText("Přihlášen: " + Protection.getVerifiedName().orElse(""));
                startButton.setDisable(false);
            } else {
                loginButton.setText("Přihlásit");
                startButton.setDisable(false);
            }
        });
    }

    @FXML
    void exitApp(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void initialize() {
        assert exitButton != null : "fx:id=\"exitButton1\" was not injected: check your FXML file 'mainMenu.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'mainMenu.fxml'.";
        assert startButton != null : "fx:id=\"startButton\" was not injected: check your FXML file 'mainMenu.fxml'.";

        if (Protection.isVerified()) {
            loginButton.setText("Přihlášen: " + Protection.getVerifiedName().orElse(""));
            startButton.setDisable(false);
        } else {
            loginButton.setText("Přihlásit");
            startButton.setDisable(false);
        }
    }
}
