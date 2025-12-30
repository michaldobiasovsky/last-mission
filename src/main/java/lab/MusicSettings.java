package lab;

import lab.score.ScoreException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public final class MusicSettings {

    private static final String FILE_NAME = "scores.csv";
    private static final String MUSIC_ON = "MUSIC ON";
    private static final String MUSIC_OFF = "MUSIC OFF";

    private MusicSettings() { }

    public static Optional<Boolean> loadMusicSetting() {
        Path p = Paths.get(FILE_NAME);
        if (!Files.exists(p)) return Optional.empty();

        try {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);

            for (String line : lines) {
                Optional<String> first = firstNonBlankTrimmed(line);
                if (first.isPresent()) {
                    return parseMusicSetting(first.get());
                }
            }

            return Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static Optional<String> firstNonBlankTrimmed(String line) {
        if (line == null) return Optional.empty();
        String t = line.trim();
        return t.isEmpty() ? Optional.empty() : Optional.of(t);
    }

    private static Optional<Boolean> parseMusicSetting(String t) {
        if (t.equalsIgnoreCase(MUSIC_ON)) return Optional.of(true);
        if (t.equalsIgnoreCase(MUSIC_OFF)) return Optional.of(false);
        return Optional.empty();
    }

    public static void saveMusicSetting(boolean enabled) throws ScoreException {
        Path p = Paths.get(FILE_NAME);
        String header = enabled ? MUSIC_ON : MUSIC_OFF;

        try {
            List<String> existing = Files.exists(p)
                ? Files.readAllLines(p, StandardCharsets.UTF_8)
                : List.of();

            int firstNonEmptyIdx = -1;
            for (int i = 0; i < existing.size(); i++) {
                if (!existing.get(i).trim().isEmpty()) {
                    firstNonEmptyIdx = i;
                    break;
                }
            }

            if (firstNonEmptyIdx != -1) {
                String first = existing.get(firstNonEmptyIdx).trim();
                if (first.equalsIgnoreCase(MUSIC_ON) || first.equalsIgnoreCase(MUSIC_OFF)) {
                    existing = new java.util.ArrayList<>(existing);
                    existing.remove(firstNonEmptyIdx);
                }
            }

            java.util.ArrayList<String> out = new java.util.ArrayList<>();
            out.add(header);
            out.addAll(existing);

            Files.write(p, out, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ScoreException("Error during music setting " + e.getMessage(), e);
        }
    }
}
