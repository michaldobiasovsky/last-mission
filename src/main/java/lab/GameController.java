package lab;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lab.score.Score;
import lab.score.ScoreException;
import lab.score.ScoreRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameController implements StageAware {

    private static final String DEFAULT_LEVEL_IMAGE = "/lab/level1.png";

    @FXML private BorderPane root;
    @FXML private Canvas canvas;

    @FXML private Button blockBtn;
    @FXML private Button buildBtn;
    @FXML private Button killBtn;

    @FXML private Label lemmingsCount;
    @FXML private Label time;
    @FXML private Label neededLemmings;

    private World world;
    private DrawingThread timer;
    private AnimationTimer uiUpdater;

    private Role selectedRole = null;

    private Level currentLevelObj;
    private final Map<Role, Integer> abilityCounts = new EnumMap<>(Role.class);
    private List<Score> scores = new ArrayList<>();

    private long levelStartTime = 0;
    private boolean levelFinished = false;

    @Override
    public void setStage(Stage stage) {
        // Stage reference removed as it was unused
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
    void onStop(javafx.event.ActionEvent event) {
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

        String imagePath = DEFAULT_LEVEL_IMAGE;
        if (level != null && level.getBackgroundImagePath() != null) {
            imagePath = level.getBackgroundImagePath();
        }

        java.net.URL resource = getClass().getResource(imagePath);
        if (resource != null) {
            root.setStyle(
                String.format("-fx-background-image: url('%s'); -fx-background-size: cover;", resource.toExternalForm())
            );
        } else {
            root.setStyle("");
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
                if (abilityCounts.getOrDefault(Role.BLOCK, 0) > 0 && target.becomeBlock()) {
                    decrementAbility(Role.BLOCK);
                }
                break;

            case BUILD:
                if (abilityCounts.getOrDefault(Role.BUILD, 0) > 0 && target.buildStairs(world, 6)) {
                    decrementAbility(Role.BUILD);
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
        lemmingsCount.setText("On track: " + onTrack);

        long elapsed = (levelStartTime > 0) ? (System.currentTimeMillis() - levelStartTime) : 0;
        time.setText("Time: " + formatTime(elapsed));

        int needed = (currentLevelObj != null) ? currentLevelObj.getNeededLemmings() : 0;
        int exited = (world != null) ? world.getExitedCount() : 0;
        neededLemmings.setText("Goal: " + exited + "/" + needed);

        if (!levelFinished && needed > 0 && exited >= needed) {
            finishLevel();
            return;
        }

        boolean outOfLemmings = (world != null) && world.isOutOfLemmings();
        if (!levelFinished && needed > 0 && exited < needed && onTrack == 0 && outOfLemmings) {
            failLevel();
        }
    }

    private void failLevel() {
        if (levelFinished) return;
        levelFinished = true;
        stop();
        javafx.application.Platform.runLater(this::showLevelFailedDialog);
    }

    private void showLevelFailedDialog() {
        javafx.scene.control.ButtonType retryButton = new javafx.scene.control.ButtonType("Retry");
        javafx.scene.control.ButtonType closeButton = new javafx.scene.control.ButtonType("Close");

        javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = createBaseDialog(
            "Level failed",
            "You lost! No astronauts left."
        );
        dialog.getDialogPane().getButtonTypes().setAll(retryButton, closeButton);

        Optional<javafx.scene.control.ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == retryButton) {
            levelFinished = false;
            startLevel(currentLevelObj);
        } else {
            App.showMainMenu();
        }
    }

    private void finishLevel() {
        if (levelFinished) return;
        levelFinished = true;
        stop();
        javafx.application.Platform.runLater(this::showLevelCompletedDialog);
    }

    private void showLevelCompletedDialog() {
        final long timeTaken = System.currentTimeMillis() - levelStartTime;
        String timeStr = formatTime(timeTaken);

        javafx.scene.control.ButtonType retryButton = new javafx.scene.control.ButtonType("Retry");
        javafx.scene.control.ButtonType nextButton = new javafx.scene.control.ButtonType("Next level");
        javafx.scene.control.ButtonType closeButton = new javafx.scene.control.ButtonType("Close");

        javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = createBaseDialog(
            "Level completed",
            "Congratulations! Level completed."
        );

        Level next = findNextLevel();
        if (next != null) {
            dialog.getDialogPane().getButtonTypes().setAll(retryButton, nextButton, closeButton);
        } else {
            dialog.getDialogPane().getButtonTypes().setAll(retryButton, closeButton);
        }

        TextField nameField = setupScoreInput(dialog, timeStr);
        Optional<javafx.scene.control.ButtonType> result = dialog.showAndWait();

        String playerName = (nameField.getText() != null && !nameField.getText().trim().isEmpty())
            ? nameField.getText().trim()
            : "Anonymous";

        onLevelCompleted(playerName, timeTaken);

        handleLevelCompletionResult(result, retryButton, nextButton, next);
    }

    private TextField setupScoreInput(javafx.scene.control.Dialog<?> dialog, String timeStr) {
        Label info = new Label("Your time: " + timeStr + "\nEnter player name for the leaderboard:");
        TextField nameField = new TextField("Anonymous");

        VBox content = new VBox(8);
        content.getChildren().addAll(info, nameField);
        dialog.getDialogPane().setContent(content);
        return nameField;
    }

    private void handleLevelCompletionResult(Optional<javafx.scene.control.ButtonType> result,
                                             javafx.scene.control.ButtonType retryBtn,
                                             javafx.scene.control.ButtonType nextBtn,
                                             Level nextLevel) {
        if (result.isPresent() && result.get() == retryBtn) {
            levelFinished = false;
            startLevel(currentLevelObj);
        } else if (result.isPresent() && nextLevel != null && result.get() == nextBtn) {
            levelFinished = false;
            startLevel(nextLevel);
        } else {
            App.showMainMenu();
        }
    }

    private javafx.scene.control.Dialog<javafx.scene.control.ButtonType> createBaseDialog(String title, String header) {
        javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = new javafx.scene.control.Dialog<>();
        dialog.initStyle(javafx.stage.StageStyle.UNDECORATED);

        dialog.setTitle(title);
        dialog.setHeaderText(header);

        dialog.getDialogPane().getStylesheets().add(App.class.getResource("/lab/application.css").toExternalForm());
        Image iconImage = new Image(getClass().getResourceAsStream("/lab/stay.gif"));
        ImageView imageView = new ImageView(iconImage);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        dialog.setGraphic(imageView);

        try {
            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            if (dialogStage != null) {
                dialogStage.getIcons().add(iconImage);
            }
        } catch (ClassCastException | NullPointerException ignored) {
            // Best-effort: setting the dialog icon is not critical.
        }

        return dialog;
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

    private Level findNextLevel() {
        if (currentLevelObj == null) return null;
        List<Level> levels = LevelRepository.loadDefaults();
        return levels.stream()
            .filter(l -> l.getId() > currentLevelObj.getId())
            .min(Comparator.comparingInt(Level::getId))
            .orElse(null);
    }

    private void onLevelCompleted(String playerName, long elapsedMillis) {
        int lvlId = (currentLevelObj != null) ? currentLevelObj.getId() : 0;

        scores.add(new Score(lvlId, true, elapsedMillis, playerName));

        try {
            ScoreRepository.save(scores);
        } catch (ScoreException e) {
            e.printStackTrace();
        }
    }
}
