// java
// src/main/java/lab/MainMenuController.java
package lab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

            Navigation.loadScene("/lab/gameWindow.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void showLevels(ActionEvent event) {
        try {
            Navigation.loadScene("/lab/levelsSelection.fxml");
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
