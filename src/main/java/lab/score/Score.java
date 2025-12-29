package lab.score;

import lab.score.ScoreException;

import java.util.Objects;

public class Score {

    private final int level;
    private final boolean unlocked;
    private final long timeMillis;
    private final String playerName;

    public Score(int level, boolean unlocked, long timeMillis, String playerName) {
        this.level = level;
        this.unlocked = unlocked;
        this.timeMillis = timeMillis;
        this.playerName = playerName != null ? playerName : "";
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

    public String getPlayerName() {
        return playerName;
    }

    // unlocked je uložen jako 1/0, playerName escapuje '\' a ';'
    public String toCsvLine() {
        String escapedName = playerName.replace("\\", "\\\\").replace(";", "\\;");
        return String.format("%d;%s;%d;%s", level, unlocked ? "1" : "0", timeMillis, escapedName);
    }

    public static Score fromCsvLine(String line, int lineNumber) throws ScoreException {
        if (line == null) throw new ScoreException("Empty line at " + lineNumber);
        String[] parts = line.split(";", 4);
        if (parts.length < 3) {
            throw new ScoreException("Neplatný formát na řádku " + lineNumber + ": " + line);
        }
        try {
            int lvl = Integer.parseInt(parts[0].trim());
            boolean unlocked = "1".equals(parts[1].trim()) || Boolean.parseBoolean(parts[1].trim());
            long time = Long.parseLong(parts[2].trim());
            String name = "";
            if (parts.length >= 4) {
                String rawName = parts[3];
                name = rawName.replace("\\;", ";").replace("\\\\", "\\");
            }
            return new Score(lvl, unlocked, time, name);
        } catch (NumberFormatException e) {
            throw new ScoreException("Chyba parsování na řádku " + lineNumber + ": " + line, e);
        }
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
