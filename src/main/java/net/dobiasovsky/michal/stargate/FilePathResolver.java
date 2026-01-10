package net.dobiasovsky.michal.stargate;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathResolver {
    private static final String SCORES_FILE = "scores.csv";
    private static final String MUSIC_FILE = "music.dat";
    private static final String APP_DIR = "StarGate";

    private FilePathResolver() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Path getScoresFilePath() {
        return getAppFilePath(SCORES_FILE);
    }

    public static Path getMusicFilePath() {
        return getAppFilePath(MUSIC_FILE);
    }

    private static Path getAppFilePath(String fileName) {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            // Windows: AppData\Roaming\StarGate
            String appData = System.getenv("APPDATA");
            return Paths.get(appData, APP_DIR, fileName);
        } else if (osName.contains("mac")) {
            // macOS: ~/Library/Application Support/StarGate
            String home = System.getProperty("user.home");
            return Paths.get(home, "Library", "Application Support", APP_DIR, fileName);
        } else {
            // Linux: ~/.local/share/StarGate
            String home = System.getProperty("user.home");
            return Paths.get(home, ".local", "share", APP_DIR, fileName);
        }
    }
}

