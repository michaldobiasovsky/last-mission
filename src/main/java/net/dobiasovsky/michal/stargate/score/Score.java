package net.dobiasovsky.michal.stargate.score;

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
