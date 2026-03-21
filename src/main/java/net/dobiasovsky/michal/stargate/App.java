package net.dobiasovsky.michal.stargate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class App extends Application {


    public App() {
        this.appWidth = 1024;
        this.appHeight = 768;
        this.aspectRatio = appWidth / appHeight;
    }

    private final double appWidth;
    private final double appHeight;
    private final double aspectRatio;
    private final AtomicBoolean resizing = new AtomicBoolean(false);

    private Stage primaryStage;
    private MediaPlayer musicPlayer;
    @Getter
    private boolean musicEnabled = true;
    private GameController gameController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            log.info("Application startup initiated");
            this.primaryStage = primaryStage;
            primaryStage.setTitle("STARGATE");
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(appWidth / 2 + 16);
            primaryStage.setMinHeight(appHeight / 2 + 39);

            loadMusicSettingFromFile();

            switchToMainMenu();

            Image iconImage = new Image(getClass().getResourceAsStream("stay.gif"));
            primaryStage.getIcons().add(iconImage);

            primaryStage.show();
            installAspectRatioListener(primaryStage);

            setMusicEnabled(musicEnabled);
            primaryStage.setOnCloseRequest(this::exitProgram);
            log.info("Application startup completed");
        } catch (Exception e) {
            log.fatal("Failed to start application", e);
        }
    }

    private void loadMusicSettingFromFile() {
        try {
            var opt = new MusicSettings().loadMusicSetting();
            if (opt.isPresent()) {
                musicEnabled = opt.get();
                log.debug("Loaded persisted music setting: {}", musicEnabled);
            }
        } catch (Exception e) {
            // Pokud se hudební nastavení nepodaří načíst, zůstane zapnuto
            log.warn("Failed to load music setting, keeping defaults", e);
        }
    }

    public void switchToMainMenu() throws IOException {
        log.info("Switching scene to main menu");
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
        Parent root = menuLoader.load();

        MainMenuController menuController = menuLoader.getController();
        menuController.setApp(this);
        menuController.updateMusicButton();

        Scene scene = createScaledScene(root);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);
    }

    public void switchToLevelsSelection() throws IOException {
        log.info("Switching scene to levels selection");
        FXMLLoader levelsLoader = new FXMLLoader(getClass().getResource("/net/dobiasovsky/michal/stargate/levels.fxml"));
        Parent root = levelsLoader.load();

        LevelsController levelsController = levelsLoader.getController();
        levelsController.setApp(this);

        Scene scene = createScaledScene(root);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);
    }



    public void switchToGame(Level level) throws IOException {
        log.info("Switching scene to game for level {}", level);
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("gameWindow.fxml"));
        Parent root = gameLoader.load();

        gameController = gameLoader.getController();
        gameController.setApp(this);

        Scene scene = createScaledScene(root);
        applyCommonSceneStyles(scene);
        primaryStage.setScene(scene);

        gameController.startLevel(level);
    }

    private Scene createScaledScene(Parent view) {
        Group group = new Group(view);
        Pane wrapper = new Pane(group);
        wrapper.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(wrapper, appWidth, appHeight);

        Scale scale = new Scale(1, 1, 0, 0);
        group.getTransforms().setAll(scale);

        ChangeListener<Number> resize = (obs, o, n) -> {
            double sceneW = scene.getWidth();
            double sceneH = scene.getHeight();
            if (sceneH <= 0 || sceneW <= 0) return;

            double factor = sceneH / appHeight;

            scale.setX(factor);
            scale.setY(factor);

            double scaledWidth = appWidth * factor;
            double offsetX = (sceneW - scaledWidth) / 2;
            group.setLayoutX(offsetX);
        };
        scene.widthProperty().addListener(resize);
        scene.heightProperty().addListener(resize);

        Platform.runLater(() -> resize.changed(null, null, null));

        return scene;
    }

    private void installAspectRatioListener(Stage stage) {
        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) return;
            addSceneResizeListeners(stage, newScene);
        });

        if (stage.getScene() != null) {
            addSceneResizeListeners(stage, stage.getScene());
        }
    }

    private void addSceneResizeListeners(Stage stage, Scene scene) {
        scene.widthProperty().addListener((o, oldVal, newVal) -> {
            if (!resizing.compareAndSet(false, true)) return;
            Platform.runLater(() -> {
                try {
                    double decorH = stage.getHeight() - scene.getHeight();
                    double targetSceneH = scene.getWidth() / aspectRatio;
                    double targetStageH = targetSceneH + decorH;
                    if (Math.abs(stage.getHeight() - targetStageH) > 2) {
                        stage.setHeight(targetStageH);
                    }
                } finally {
                    resizing.set(false);
                }
            });
        });

        scene.heightProperty().addListener((o, oldVal, newVal) -> {
            if (!resizing.compareAndSet(false, true)) return;
            Platform.runLater(() -> {
                try {
                    double decorW = stage.getWidth() - scene.getWidth();
                    double targetSceneW = scene.getHeight() * aspectRatio;
                    double targetStageW = targetSceneW + decorW;
                    if (Math.abs(stage.getWidth() - targetStageW) > 2) {
                        stage.setWidth(targetStageW);
                    }
                } finally {
                    resizing.set(false);
                }
            });
        });
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
                log.warn("Background music file not found");
                return;
            }

            Media media = new Media(musicUrl.toExternalForm());
            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(0.3);
            musicPlayer.play();
            log.debug("Background music started");
        } catch (RuntimeException e) {
            log.error("Failed to play background music", e);
        }
    }

    private void shutdownMusicPlayer() {
        if (musicPlayer == null) {
            return;
        }
        try {
            musicPlayer.stop();
            musicPlayer.dispose();
            log.debug("Background music stopped");
        } catch (RuntimeException e) {
            log.warn("Failed to stop music player", e);
        } finally {
            musicPlayer = null;
        }
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        log.info("Music toggled: {}", enabled ? "enabled" : "disabled");
        if (enabled) {
            playBackgroundMusic();
        } else {
            shutdownMusicPlayer();
        }
    }

    @Override
    public void stop() throws Exception {
        shutdownMusicPlayer();

        if (gameController != null) {
            try {
                gameController.stop();
            } catch (RuntimeException e) {
                log.warn("Failed to stop game controller", e);
            }
        }

        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        log.info("Exit requested by user");
        System.exit(0);
    }
}
