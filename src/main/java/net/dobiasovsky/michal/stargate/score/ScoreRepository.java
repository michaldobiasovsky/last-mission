package net.dobiasovsky.michal.stargate.score;

import net.dobiasovsky.michal.stargate.FilePathResolver;
import lombok.ToString;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ToString(onlyExplicitlyIncluded = true)
public class ScoreRepository {

    public void save(List<Score> scores) throws ScoreException {
        Path file = FilePathResolver.getScoresFilePath();
        file.getParent().toFile().mkdirs();

        try {
            List<String> out = new ArrayList<>();
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
        Path file = FilePathResolver.getScoresFilePath();
        if (!Files.exists(file)) {
            return result;
        }

        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            int lineNumber = 0;
            for (String line : lines) {
                lineNumber++;
                if (isValidScoreLine(line)) {
                    result.add(new Score(line, lineNumber));
                }
            }
        } catch (IOException e) {
            throw new ScoreException("Exception during loading: " + e.getMessage(), e);
        }
        return result;
    }

    private boolean isValidScoreLine(String line) {
        return line != null && !line.trim().isEmpty();
    }
}
