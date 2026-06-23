package net.dobiasovsky.lastmission.score;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JpaConnectorTest {

    @TempDir
    Path tempDir;

    @Test
    void createsSchemaInFreshDatabaseAndPersistsScores() {
        String originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tempDir.toString());

        try (JpaConnector connector = new JpaConnector()) {
            connector.replaceAll(List.of(new Score(3, true, 1234L, "Ada")));

            List<Score> loaded = connector.loadAll();

            assertEquals(1, loaded.size());
            Score score = loaded.getFirst();
            assertNotNull(score.getPlayer());
            assertEquals(3, score.getLevel());
            assertTrue(score.isUnlocked());
            assertEquals(1234L, score.getTime());
            assertEquals("Ada", score.getPlayerName());
        } finally {
            if (originalHome != null) {
                System.setProperty("user.home", originalHome);
            }
        }
    }
}

