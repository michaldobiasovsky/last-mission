package lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final double APP_WIDTH = 1024;
    private static final double APP_HEIGHT = 768;

    private Stage primaryStage;
    private MediaPlayer musicPlayer;
    private boolean musicEnabled = true;
    private GameController gameController;
    private static App instance;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            instance = this;
            this.primaryStage = primaryStage;
            primaryStage.setTitle("STARGATE");

            loadFont();
            switchToMainMenu();
            setMusicEnabled(musicEnabled);

            Image iconImage = new Image(getClass().getResourceAsStream("/lab/stay.gif"));
            primaryStage.getIcons().add(iconImage);

            primaryStage.show();
            primaryStage.setOnCloseRequest(this::exitProgram);
        } catch (Exception e) {
            LOGGER.severe("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFont() {
        try {
            Font.loadFont(getClass().getResourceAsStream("/TRON.TTF"), 20);
        } catch (Exception e) {
            LOGGER.warning("Failed to load custom font: " + e.getMessage());
        }
    }

    public void switchToMainMenu() throws IOException {
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/lab/mainMenu.fxml"));
        Parent root = menuLoader.load();

        MainMenuController menuController = menuLoader.getController();
        menuController.setApp(this);

        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    public void switchToLevelsSelection() throws IOException {
        FXMLLoader levelsLoader = new FXMLLoader(getClass().getResource("/lab/levels.fxml"));
        Parent root = levelsLoader.load();

        LevelsController levelsController = levelsLoader.getController();
        levelsController.setApp(this);

        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
    }

    public void switchToGame(Level level) throws IOException {
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/lab/gameWindow.fxml"));
        Parent root = gameLoader.load();

        gameController = gameLoader.getController();
        gameController.setApp(this);

        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();

        gameController.startLevel(level);
    }

    private void applyCommonSceneStyles(Scene scene) {
        URL cssUrl = getClass().getResource("/lab/application.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    private void playBackgroundMusic() {
        try {
            if (musicPlayer != null) {
                musicPlayer.dispose();
            }

            URL musicUrl = getClass().getResource("/lab/cosmo.mp3");
            if (musicUrl == null) {
                LOGGER.warning("Background music file not found");
                return;
            }

            Media media = new Media(musicUrl.toExternalForm());
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(0.3);
            musicPlayer.play();
        } catch (RuntimeException e) {
            LOGGER.warning("Failed to play background music: " + e.getMessage());
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
            LOGGER.warning("Failed to stop music player: " + e.getMessage());
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

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static App getInstance() {
        return instance;
    }

    @Override
    public void stop() throws Exception {
        shutdownMusicPlayer();

        if (gameController != null) {
            try {
                gameController.stop();
            } catch (RuntimeException e) {
                LOGGER.warning("Failed to stop game controller: " + e.getMessage());
            }
        }

        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        System.exit(0);
    }
}
