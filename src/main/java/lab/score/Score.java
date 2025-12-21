package lab.score;

import java.util.Objects;

public class Score {

    private final int level;
    private final boolean unlocked;
    private final long timeMillis;

    public Score(int level, boolean unlocked, long timeMillis) {
        this.level = level;
        this.unlocked = unlocked;
        this.timeMillis = timeMillis;
    }

    public int getLevel() {
        return level;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public long getTime() {
        return this.timeMillis;
    }

    public String toCsvLine() {
        return String.format("%d;%b;%d", level, unlocked, timeMillis);
    }

    public static Score fromCsvLine(String line, int lineNumber) throws ScoreException {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new ScoreException("Neplatný formát na řádku " + lineNumber + ": " + line);
        }
        try {
            int lvl = Integer.parseInt(parts[0].trim());
            boolean unlocked = Boolean.parseBoolean(parts[1].trim());
            long time = Long.parseLong(parts[2].trim());
            return new Score(lvl, unlocked, time);
        } catch (NumberFormatException ex) {
            throw new ScoreException("Chyba parsování na řádku " + lineNumber + ": " + line, ex);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score)) return false;
        Score score = (Score) o;
        return level == score.level &&
            unlocked == score.unlocked &&
            timeMillis == score.timeMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, unlocked, timeMillis);
    }

    @Override
    public String toString() {
        return String.format("Score{level=%d, unlocked=%b, timeMillis=%d}", level, unlocked, timeMillis);
    }
}
