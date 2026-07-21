package net.dobiasovsky.lastmission;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathResolver {
    private static final String SCORES_FILE = "scores.csv";
    private static final String MUSIC_FILE = "music.dat";
    private static final String DB_FILE = "score-db"; // Soubor databáze bez .mv.db
    private static final String APP_DIR = "LastMission";

    private FilePathResolver() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Path getScoresFilePath() {
        return getAppFilePath(SCORES_FILE);
    }

    public static Path getMusicFilePath() {
        return getAppFilePath(MUSIC_FILE);
    }

    public static Path getDatabaseFilePath() {
        Path dbPath = getAppFilePath(DB_FILE);

        // Zajištění, že cílová složka (např. ~/.local/share/LastMission) existuje
        try {
            Files.createDirectories(dbPath.getParent());
        } catch (IOException e) {
            System.err.println("Nepodařilo se vytvořit složku aplikace: " + e.getMessage());
        }

        return dbPath;
    }

    private static Path getAppFilePath(String fileName) {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            // Windows: AppData\Roaming\LastMission
            String appData = System.getenv("APPDATA");
            if (hasText(appData)) {
                return Paths.get(appData, APP_DIR, fileName);
            }

            String home = System.getProperty("user.home");
            return Paths.get(home, "AppData", "Roaming", APP_DIR, fileName);
        } else if (osName.contains("mac")) {
            // macOS: ~/Library/Application Support/LastMission
            String home = System.getProperty("user.home");
            return Paths.get(home, "Library", "Application Support", APP_DIR, fileName);
        } else {
            String xdgDataHome = System.getenv("XDG_DATA_HOME");
            if (hasText(xdgDataHome)) {
                return Paths.get(xdgDataHome, APP_DIR, fileName);
            }

            // Linux: ~/.local/share/LastMission
            String home = System.getProperty("user.home");
            return Paths.get(home, ".local", "share", APP_DIR, fileName);
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}