// java
package lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

    private GameController gameController;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/lab/mainMenu.fxml"));
            Parent root = menuLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/lab/application.css").toExternalForm());
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/lab/stay.gif")));
            primaryStage.setScene(scene);
            primaryStage.resizableProperty().set(false);
            primaryStage.setTitle("Stargate");
            primaryStage.show();
            primaryStage.setOnCloseRequest(this::exitProgram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        if (gameController != null) {
            gameController.stop();
        }
        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        System.exit(0);
    }
}
