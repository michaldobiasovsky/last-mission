package net.dobiasovsky.michal.stargate.score;

import lombok.ToString;
import java.util.List;

@ToString(onlyExplicitlyIncluded = true)
public class ScoreRepository {

    public void save(List<Score> scores) throws ScoreException {
        try (JpaConnector connector = new JpaConnector()) {
            connector.replaceAll(scores);
        } catch (RuntimeException e) {
            throw new ScoreException("Exception during saving: " + e.getMessage(), e);
        }
    }

    public List<Score> load() throws ScoreException {
        try (JpaConnector connector = new JpaConnector()) {
            return connector.loadAll();
        } catch (RuntimeException e) {
            throw new ScoreException("Exception during loading: " + e.getMessage(), e);
        }
    }
}
