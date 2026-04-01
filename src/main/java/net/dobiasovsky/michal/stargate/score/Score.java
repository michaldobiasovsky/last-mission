package net.dobiasovsky.michal.stargate.score;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    public Score() {
        // Required by JPA
    }

    public Score(int level, boolean unlocked, long timeMillis, String playerName) {
        this(level, unlocked, timeMillis, new Player(playerName));
    }

    public Score(int level, boolean unlocked, long timeMillis, Player player) {
        this.level = level;
        this.unlocked = unlocked;
        this.timeMillis = timeMillis;
        this.player = player != null ? player : new Player("");
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
            this.player = new Player(name);
        } catch (NumberFormatException e) {
            throw new ScoreException("Parsing error at line " + lineNumber + ": " + line, e);
        }
    }


    public long getTime() {
        return this.timeMillis;
    }

    public String getPlayerName() {
        return player != null ? player.getName() : "";
    }

    public String toCsvLine() {
        String escapedName = getPlayerName().replace("\\", "\\\\").replace(";", "\\;");
        return String.format("%d;%s;%d;%s", level, unlocked ? "1" : "0", timeMillis, escapedName);
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score)) return false;
        Score score = (Score) o;
        return level == score.level &&
            unlocked == score.unlocked &&
            timeMillis == score.timeMillis &&
            Objects.equals(getPlayerName(), score.getPlayerName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, unlocked, timeMillis, getPlayerName());
    }

    @Override
    public String toString() {
        return String.format("Score{level=%d, unlocked=%b, timeMillis=%d, playerName=%s}",
            level, unlocked, timeMillis, getPlayerName());
    }
}
