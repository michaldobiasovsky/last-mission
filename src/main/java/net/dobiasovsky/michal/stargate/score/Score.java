package net.dobiasovsky.michal.stargate.score;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import java.util.Objects;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false)
    private int level;
    @Getter
    @Column(nullable = false)
    private boolean unlocked;
    @Getter
    @Column(nullable = false)
    private long timeMillis;
    @Getter
    @Column(nullable = false)
    private String playerName;

    public Score() {
        // Required by JPA
    }

    public Score(int level, boolean unlocked, long timeMillis, String playerName) {
        this.level = level;
        this.unlocked = unlocked;
        this.timeMillis = timeMillis;
        this.playerName = playerName != null ? playerName : "";
    }

    public Score(String line, int lineNumber) throws ScoreException {
        if (line == null) throw new ScoreException("Empty line at " + lineNumber);
        String[] parts = line.split(";", 4);
        if (parts.length < 3) {
            throw new ScoreException("Invalid format at line " + lineNumber + ": " + line);
        }
        try {
            this.level = Integer.parseInt(parts[0].trim());
            this.unlocked = "1".equals(parts[1].trim()) || Boolean.parseBoolean(parts[1].trim());
            this.timeMillis = Long.parseLong(parts[2].trim());
            String name = "";
            if (parts.length >= 4) {
                String rawName = parts[3];
                name = rawName.replace("\\;", ";").replace("\\\\", "\\");
            }
            this.playerName = name;
        } catch (NumberFormatException e) {
            throw new ScoreException("Parsing error at line " + lineNumber + ": " + line, e);
        }
    }


    public long getTime() {
        return this.timeMillis;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String toCsvLine() {
        String escapedName = playerName.replace("\\", "\\\\").replace(";", "\\;");
        return String.format("%d;%s;%d;%s", level, unlocked ? "1" : "0", timeMillis, escapedName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score)) return false;
        Score score = (Score) o;
        return level == score.level &&
            unlocked == score.unlocked &&
            timeMillis == score.timeMillis &&
            Objects.equals(playerName, score.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, unlocked, timeMillis, playerName);
    }

    @Override
    public String toString() {
        return String.format("Score{level=%d, unlocked=%b, timeMillis=%d, playerName=%s}",
            level, unlocked, timeMillis, playerName);
    }
}
