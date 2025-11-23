package lab.score;

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
        } catch (NumberFormatException e) {
            throw new ScoreException("Neplatná čísla na řádku " + lineNumber + ": " + line, e);
        }
    }

    public String toCsvLine() {
        return level + ";" + unlocked + ";" + timeMillis;
    }
}
