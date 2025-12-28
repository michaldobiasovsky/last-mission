package lab;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class for handling scene navigation on the primary stage.
 */
public class Navigation {
    
    private static Stage primaryStage;
    private static GameController currentGameController;
    
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void loadScene(String fxmlPath) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set");
        }
        
        // Stop current game if running
        if (currentGameController != null) {
            currentGameController.stop();
            currentGameController = null;
        }
        
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }
    
    public static void loadGameScene(Level level) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set");
        }
        
        // Stop current game if running
        if (currentGameController != null) {
            currentGameController.stop();
        }
        
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource("/lab/gameWindow.fxml"));
        Parent root = loader.load();
        GameController controller = loader.getController();
        currentGameController = controller;
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        
        // Start level after scene is set
        javafx.application.Platform.runLater(() -> controller.startLevel(level));
    }
    
    public static void setGameController(GameController controller) {
        currentGameController = controller;
    }
    
    public static GameController getCurrentGameController() {
        return currentGameController;
    }
}
