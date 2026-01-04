package lab.score;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ScoreRepository {

    private static final String FILE_NAME = "scores.csv";
    private static final String MUSIC_ON = "MUSIC ON";
    private static final String MUSIC_OFF = "MUSIC OFF";

    public void save(List<Score> scores) throws ScoreException {
        Path file = Paths.get(FILE_NAME);
        String existingHeader = null;

        try {
            if (Files.exists(file)) {
                List<String> existing = Files.readAllLines(file, StandardCharsets.UTF_8);
                for (String l : existing) {
                    if (l == null) continue;
                    String t = l.trim();
                    if (t.isEmpty()) continue;
                    if (t.equalsIgnoreCase(MUSIC_ON) || t.equalsIgnoreCase(MUSIC_OFF)) {
                        existingHeader = t;
                    }
                    break;
                }
            }

            List<String> out = new ArrayList<>();
            if (existingHeader != null) out.add(existingHeader);
            for (Score s : scores) {
                out.add(s.toCsvLine());
            }
            Files.write(file, out, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ScoreException("Exception during saving: " + e.getMessage(), e);
        }
    }

    public List<Score> load() throws ScoreException {
        List<Score> result = new ArrayList<>();
        Path file = Paths.get(FILE_NAME);
        if (!Files.exists(file)) {
            return result;
        }

        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            int lineNumber = 0;
            for (String line : lines) {
                lineNumber++;
                if (line == null) continue;
                if (line.trim().isEmpty()) continue;
                String t = line.trim();
                if (t.equalsIgnoreCase(MUSIC_ON) || t.equalsIgnoreCase(MUSIC_OFF)) {
                    continue;
                }
                result.add(new Score(line, lineNumber));
            }
        } catch (IOException e) {
            throw new ScoreException("Exception during loading: " + e.getMessage(), e);
        }
        return result;
    }
}
