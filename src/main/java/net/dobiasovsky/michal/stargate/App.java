package net.dobiasovsky.michal.stargate;

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

    private final Logger logger = Logger.getLogger(getClass().getName());

    public App() {
        this.appWidth = 1024;
        this.appHeight = 768;
    }

    private final double appWidth;
    private final double appHeight;

    private Stage primaryStage;
    private MediaPlayer musicPlayer;
    private boolean musicEnabled = true;
    private GameController gameController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            primaryStage.setTitle("STARGATE");
            primaryStage.setResizable(false);

            switchToMainMenu();
            setMusicEnabled(musicEnabled);

            Image iconImage = new Image(getClass().getResourceAsStream("stay.gif"));
            primaryStage.getIcons().add(iconImage);

            primaryStage.show();
            primaryStage.setOnCloseRequest(this::exitProgram);
        } catch (Exception e) {
            logger.severe("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void switchToMainMenu() throws IOException {
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
        Parent root = menuLoader.load();

        MainMenuController menuController = menuLoader.getController();
        menuController.setApp(this);

        Scene scene = new Scene(root, appWidth, appHeight);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);
    }

    public void switchToLevelsSelection() throws IOException {
        FXMLLoader levelsLoader = new FXMLLoader(getClass().getResource("/net/dobiasovsky/michal/stargate/levels.fxml"));
        Parent root = levelsLoader.load();

        LevelsController levelsController = levelsLoader.getController();
        levelsController.setApp(this);

        Scene scene = new Scene(root, appWidth, appHeight);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);
    }



    public void switchToGame(Level level) throws IOException {
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("gameWindow.fxml"));
        Parent root = gameLoader.load();

        gameController = gameLoader.getController();
        gameController.setApp(this);

        Scene scene = new Scene(root, appWidth, appHeight);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();

        gameController.startLevel(level);
    }

    private void applyCommonSceneStyles(Scene scene) {
        URL cssUrl = getClass().getResource("application.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    private void playBackgroundMusic() {
        try {
            if (musicPlayer != null) {
                musicPlayer.dispose();
            }

            URL musicUrl = getClass().getResource("/net/dobiasovsky/michal/stargate/cosmo.mp3");
            if (musicUrl == null) {
                logger.warning("Background music file not found");
                return;
            }

            Media media = new Media(musicUrl.toExternalForm());
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(0.3);
            musicPlayer.play();
        } catch (RuntimeException e) {
            logger.warning("Failed to play background music: " + e.getMessage());
        }
    }

    private void shutdownMusicPlayer() {
        if (musicPlayer == null) {
            return;
        }
        try {
            musicPlayer.stop();
            musicPlayer.dispose();
        } catch (RuntimeException e) {
            logger.warning("Failed to stop music player: " + e.getMessage());
        } finally {
            musicPlayer = null;
        }
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (enabled) {
            playBackgroundMusic();
        } else {
            shutdownMusicPlayer();
        }
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    @Override
    public void stop() throws Exception {
        shutdownMusicPlayer();

        if (gameController != null) {
            try {
                gameController.stop();
            } catch (RuntimeException e) {
                logger.warning("Failed to stop game controller: " + e.getMessage());
            }
        }

        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        System.exit(0);
    }
}
