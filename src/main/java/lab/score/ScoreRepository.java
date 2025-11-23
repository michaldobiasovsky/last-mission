package lab.score;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ScoreRepository {

    private static final String FILE_NAME = "scores.csv";

    public static void save(List<Score> scores) throws ScoreException {
        File file = new File(FILE_NAME);
        try (BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {

            for (Score s : scores) {
                bw.write(s.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ScoreException("Exception during saving: " + e.getMessage(), e);
        }
    }

    public static List<Score> load() throws ScoreException {
        List<Score> result = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return result;
        }

        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                result.add(Score.fromCsvLine(line, lineNumber));
            }
        } catch (IOException e) {
            throw new ScoreException("Exception during loading: " + e.getMessage(), e);
        }
        return result;
    }
}
