package lab;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController implements StageAware {

    @FXML private BorderPane root;
    @FXML private Canvas canvas;

    @FXML private Button blockBtn;
    @FXML private Button buildBtn;
    @FXML private Button killBtn;

    @FXML private Label LemmingsCount;
    @FXML private Label time;
    @FXML private Label NeadedLemmings;

    private Stage stage;

    private World world;
    private DrawingThread timer;
    private AnimationTimer uiUpdater;

    private Role selectedRole = null;

    private Level currentLevelObj;
    private final Map<Role, Integer> abilityCounts = new HashMap<>();
    private List<Score> scores = new ArrayList<>();

    private long levelStartTime = 0;
    private boolean levelFinished = false;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void initialize() {
        try {
            scores = ScoreRepository.load();
        } catch (ScoreException e) {
            scores = new ArrayList<>();
        }
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleCanvasClick);
        updateAbilityButtons();
    }

    @FXML
    void block(javafx.event.ActionEvent event) {
        selectedRole = Role.BLOCK;
        updateAbilityButtons();
    }

    @FXML
    void build(javafx.event.ActionEvent event) {
        selectedRole = Role.BUILD;
        updateAbilityButtons();
    }

    @FXML
    void kill(javafx.event.ActionEvent event) {
        selectedRole = Role.KILL;
        updateAbilityButtons();
    }

    @FXML
    void reset(javafx.event.ActionEvent event) {
        if (currentLevelObj != null) {
            startLevel(currentLevelObj);
        }
    }

    @FXML
    void stop(javafx.event.ActionEvent event) {
        stop();
        App.showMainMenu();
    }

    public void stop() {
        if (timer != null) timer.stop();
        if (uiUpdater != null) uiUpdater.stop();
    }

    public void startLevel(Level level) {
        if (timer != null) timer.stop();
        if (uiUpdater != null) uiUpdater.stop();

        currentLevelObj = level;
        levelFinished = false;

        world = (level != null)
            ? World.fromLevel(level, canvas.getWidth(), canvas.getHeight())
            : new World(canvas.getWidth(), canvas.getHeight());

        abilityCounts.clear();
        if (level != null && level.getAbilityCounts() != null) {
            abilityCounts.putAll(level.getAbilityCounts());
        } else {
            abilityCounts.put(Role.BLOCK, 0);
            abilityCounts.put(Role.BUILD, 0);
            abilityCounts.put(Role.KILL, 0);
        }

        updateAbilityButtons();

        applyLevelBackground(level);

        timer = new DrawingThread(canvas, world);
        timer.start();

        levelStartTime = System.currentTimeMillis();
        startUiUpdater();
    }

    private void applyLevelBackground(Level level) {
        if (root == null) return;

        String imagePath = "/lab/level1.png";

        if (level != null && level.getBackgroundImagePath() != null) {
            imagePath = level.getBackgroundImagePath();
        }

        java.net.URL resource = getClass().getResource(imagePath);
        if (resource != null) {
            root.setStyle(
                "-fx-background-image: url('" + resource.toExternalForm() + "');" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-position: center center;" +
                    "-fx-background-size: cover;"
            );
        } else {
            System.err.println("Background image not found: " + imagePath);
        }
    }

    private void handleCanvasClick(MouseEvent e) {
        if (world == null || selectedRole == null) return;
        double clickX = e.getX();
        double clickY = canvas.getHeight() - e.getY();

        Lemming target = world.getLemmings().stream()
            .filter(l -> l.getBoundingBox().contains(clickX, clickY))
            .findFirst().orElse(null);
        if (target == null) return;

        switch (selectedRole) {
            case BLOCK:
                if (decrementAbility(Role.BLOCK)) {
                    target.becomeBlock();
                }
                break;
            case BUILD:
                if (decrementAbility(Role.BUILD)) {
                    target.buildStairs(world, 3);
                }
                break;
            case KILL:
                if (decrementAbility(Role.KILL)) {
                    world.getLemmings().remove(target);
                }
                break;
            default:
                break;
        }
        updateAbilityButtons();
    }

    private boolean decrementAbility(Role r) {
        Integer cnt = abilityCounts.getOrDefault(r, 0);
        if (cnt == null || cnt <= 0) return false;
        abilityCounts.put(r, cnt - 1);
        return true;
    }

    private void startUiUpdater() {
        uiUpdater = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateUi();
            }
        };
        uiUpdater.start();
    }

    private void updateUi() {
        int onTrack = (world != null) ? world.getLemmings().size() : 0;
        LemmingsCount.setText("Na trati: " + onTrack);

        long elapsed = (levelStartTime > 0) ? (System.currentTimeMillis() - levelStartTime) : 0;
        time.setText("Čas: " + formatTime(elapsed));

        int needed = (currentLevelObj != null) ? currentLevelObj.getNeededLemmings() : 0;
        int exited = (world != null) ? world.getExitedCount() : 0;
        NeadedLemmings.setText("Cíl: " + exited + "/" + needed);

        if (!levelFinished && needed > 0 && exited >= needed) {
            finishLevel();
        }
    }

    private String formatTime(long ms) {
        long sec = ms / 1000;
        long min = sec / 60;
        long rem = sec % 60;
        return String.format("%02d:%02d", min, rem);
    }

    private void updateAbilityButtons() {
        updateButtonForRole(Role.BLOCK, blockBtn, "Block");
        updateButtonForRole(Role.BUILD, buildBtn, "Build");
        updateButtonForRole(Role.KILL, killBtn, "Kill");
    }

    private void updateButtonForRole(Role r, Button btn, String baseText) {
        int cnt = abilityCounts.getOrDefault(r, 0);
        btn.setText(baseText + " (" + cnt + ")");
        btn.setDisable(cnt <= 0);
        btn.setStyle((selectedRole == r) ? "-fx-background-color: #00C853;" : "-fx-background-color: RED;");
    }

    private void onLevelCompleted() {
        long elapsed = System.currentTimeMillis() - levelStartTime;
        int lvlId = (currentLevelObj != null) ? currentLevelObj.getId() : 0;

        scores.removeIf(s -> s.getLevel() == lvlId);
        scores.add(new Score(lvlId, true, elapsed));

        try {
            ScoreRepository.save(scores);
        } catch (ScoreException e) {
        }
    }

    private void finishLevel() {
        if (levelFinished) return;
        levelFinished = true;

        if (timer != null) timer.stop();
        if (uiUpdater != null) uiUpdater.stop();

        onLevelCompleted();

        javafx.application.Platform.runLater(() -> {
            long timeTaken = System.currentTimeMillis() - levelStartTime;
            String timeStr = formatTime(timeTaken);

            javafx.scene.control.ButtonType retryButton = new javafx.scene.control.ButtonType("Zopakovat");
            javafx.scene.control.ButtonType closeButton = new javafx.scene.control.ButtonType("Zavřít");

            Alert done = new Alert(Alert.AlertType.CONFIRMATION);
            done.setTitle("Level dokončen");
            done.setHeaderText("Gratulace! Level byl dokončen.");
            done.setContentText("Váš čas: " + timeStr + "\nCo chcete dělat dále?");

            Image iconImage = new Image(getClass().getResourceAsStream("/lab/stay.gif"));
            ImageView imageView = new ImageView(iconImage);
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            done.setGraphic(imageView);

            Stage dialogStage = (Stage) done.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(iconImage);

            done.getButtonTypes().setAll(retryButton, closeButton);

            java.util.Optional<javafx.scene.control.ButtonType> result = done.showAndWait();

            if (result.isPresent() && result.get() == retryButton) {
                levelFinished = false;
                startLevel(currentLevelObj);
            } else {
                App.showMainMenu();
            }
        });
    }
}
