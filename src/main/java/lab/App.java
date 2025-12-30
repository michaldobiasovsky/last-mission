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

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class App extends Application {

    public static final double APP_WIDTH = 1024;
    public static final double APP_HEIGHT = 768;

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private static Stage primaryStage;
    private static MediaPlayer musicPlayer;
    private static boolean musicEnabled = true;

    private GameController gameController;

    private static synchronized void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static synchronized Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setScene(Scene scene) {
        Stage stage = getPrimaryStage();
        if (stage == null) return;
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
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
            applyCommonStageSettings(getPrimaryStage());
            setScene(scene);
        } catch (RuntimeException | IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to show main menu", e);
        }
    }

    public static void showGame(lab.Level level) {
        try {
            Stage stage = getPrimaryStage();
            if (stage == null) return;

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/lab/gameWindow.fxml"));
            Parent root = loader.load();

            GameController gc = loader.getController();
            if (gc instanceof StageAware sa) {
                sa.setStage(stage);
            }

            Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
            applyCommonSceneStyles(scene);

            applyCommonStageSettings(stage);
            stage.setScene(scene);
            stage.sizeToScene();

            gc.startLevel(level);

        } catch (RuntimeException | IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to start game", e);
        }
    }

    public static Scene loadScene(String fxmlPath) throws IOException {
        URL fxmlUrl = App.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new IOException("FXML resource not found: " + fxmlPath);
        }

        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        applyCommonSceneStyles(scene);
        return scene;
    }

    public static void showLevelsSelection() {
        try {
            Scene scene = loadScene("/lab/levels.fxml");
            applyCommonStageSettings(getPrimaryStage());
            setScene(scene);
        } catch (RuntimeException | IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to show level selection", e);
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            setPrimaryStage(stage);

            playBackgroundMusic();

            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/lab/mainMenu.fxml"));
            Parent root = menuLoader.load();

            Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
            applyCommonSceneStyles(scene);

            Object controller = menuLoader.getController();
            if (controller instanceof StageAware sa) {
                sa.setStage(getPrimaryStage());
            }

            Stage ps = getPrimaryStage();
            if (ps == null) return;

            ps.getIcons().add(new Image(getClass().getResourceAsStream("/lab/stay.gif")));
            ps.setTitle("Stargate");
            ps.setScene(scene);

            applyCommonStageSettings(ps);

            ps.sizeToScene();

            ps.show();
            ps.setOnCloseRequest(this::exitProgram);
        } catch (RuntimeException | IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to start application", e);
        }
    }

    private static void playBackgroundMusic() {
        try {
            if (!musicEnabled) return;

            synchronized (App.class) {
                if (musicPlayer != null) return;

                URL musicResource = App.class.getResource("/lab/cosmo.mp3");
                if (musicResource == null) {
                    LOGGER.log(java.util.logging.Level.WARNING, "Background music resource not found: /lab/cosmo.mp3");
                    return;
                }

                Media media = new Media(musicResource.toExternalForm());
                musicPlayer = new MediaPlayer(media);
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                musicPlayer.play();
            }
        } catch (RuntimeException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to play background music", e);
        }
    }

    private static synchronized void shutdownMusicPlayer() {
        if (musicPlayer == null) return;
        try {
            musicPlayer.stop();
            musicPlayer.dispose();
        } catch (RuntimeException e) {
            LOGGER.log(java.util.logging.Level.WARNING, "Failed to stop music player", e);
        } finally {
            musicPlayer = null;
        }
    }

    public static void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (enabled) {
            playBackgroundMusic();
        } else {
            shutdownMusicPlayer();
        }
    }

    public static boolean isMusicEnabled() {
        return musicEnabled;
    }

    @Override
    public void stop() {
        shutdownMusicPlayer();

        if (gameController != null) {
            try {
                gameController.stop();
            } catch (RuntimeException e) {
                LOGGER.log(java.util.logging.Level.WARNING, "Failed to stop game controller on shutdown", e);
            }
        }

        try {
            super.stop();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.WARNING, "Failed to stop application cleanly", e);
        }
    }

    private void exitProgram(WindowEvent evt) {
        System.exit(0);
    }
}
