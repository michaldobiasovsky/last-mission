// src/main/java/lab/App.java
package lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class App extends Application {

    // Tyto rozměry nyní budou platit pro VNITŘNÍ obsah okna (Canvas/Scénu)
    public static final double APP_WIDTH = 1025;
    public static final double APP_HEIGHT = 770;

    private static Stage primaryStage;
    private static MediaPlayer musicPlayer;
    private GameController gameController;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setScene(Scene scene) {
        if (primaryStage == null) return;
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    public static void applyCommonStageSettings(Stage stage) {
        stage.setResizable(false);
    }

    public static void applyCommonSceneStyles(Scene scene) {
        scene.getStylesheets().add(App.class.getResource("/lab/application.css").toExternalForm());
    }

    public static void showMainMenu() {
        try {
            Scene scene = loadScene("/lab/mainMenu.fxml");
            applyCommonStageSettings(primaryStage);
            setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showGame(Level level) {
        try {
            if (primaryStage == null) return;

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/lab/gameWindow.fxml"));
            Parent root = loader.load();

            GameController gc = loader.getController();
            if (gc instanceof StageAware sa) {
                sa.setStage(primaryStage);
            }

            // Scéna má požadovanou velikost obsahu
            Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
            applyCommonSceneStyles(scene);

            applyCommonStageSettings(primaryStage);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene(); // Okno se zvětší o rámečky

            gc.startLevel(level);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Scene loadScene(String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
        Parent root = loader.load();
        // Scéna má požadovanou velikost obsahu
        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        applyCommonSceneStyles(scene);

        Object controller = loader.getController();
        if (controller instanceof StageAware sa) {
            sa.setStage(primaryStage);
        }

        return scene;
    }

    public static void showLevelsSelection() {
        try {
            Scene scene = loadScene("/lab/levelsSelection.fxml");
            applyCommonStageSettings(primaryStage);
            setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;

            playBackgroundMusic();

            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/lab/mainMenu.fxml"));
            Parent root = menuLoader.load();

            // Vytvoříme scénu s přesnou velikostí obsahu
            Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
            applyCommonSceneStyles(scene);

            Object controller = menuLoader.getController();
            if (controller instanceof StageAware sa) {
                sa.setStage(primaryStage);
            }

            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/lab/stay.gif")));
            primaryStage.setTitle("Stargate");
            primaryStage.setScene(scene);

            applyCommonStageSettings(primaryStage);

            // Důležité pro první spuštění:
            primaryStage.sizeToScene();

            primaryStage.show();
            primaryStage.setOnCloseRequest(this::exitProgram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playBackgroundMusic() {
        try {
            URL musicResource = getClass().getResource("/lab/cosmo.mp3");

            if (musicResource != null) {
                Media media = new Media(musicResource.toExternalForm());
                musicPlayer = new MediaPlayer(media);
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                musicPlayer.setVolume(0.3);
                musicPlayer.play();
            } else {
                System.out.println("Music not found!");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
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
