package net.dobiasovsky.michal.stargate;

import net.dobiasovsky.michal.stargate.score.ScoreException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class MusicSettings {

    private final String musicOn;
    private final String musicOff;

    public MusicSettings() {
        this.musicOn = "MUSIC ON";
        this.musicOff = "MUSIC OFF";
    }

    public Optional<Boolean> loadMusicSetting() {
        Path p = FilePathResolver.getMusicFilePath();
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

    private Optional<String> firstNonBlankTrimmed(String line) {
        if (line == null) return Optional.empty();
        String t = line.trim();
        return t.isEmpty() ? Optional.empty() : Optional.of(t);
    }

    private Optional<Boolean> parseMusicSetting(String t) {
        if (t.equalsIgnoreCase(musicOn)) return Optional.of(true);
        if (t.equalsIgnoreCase(musicOff)) return Optional.of(false);
        return Optional.empty();
    }

    public void saveMusicSetting(boolean enabled) throws ScoreException {
        Path p = FilePathResolver.getMusicFilePath();
        p.getParent().toFile().mkdirs();
        String content = enabled ? musicOn : musicOff;

        try {
            Files.write(p, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ScoreException("Error during music setting " + e.getMessage(), e);
        }
    }
}
