package net.dobiasovsky.lastmission.score;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JpaConnector implements AutoCloseable {

    private static final String PERSISTENCE_UNIT = "java2";
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
        Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);

    private final EntityManager entityManager;

    public JpaConnector() {
        this.entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public List<Score> loadAll() {
        return entityManager.createQuery(
                "select s from Score s join fetch s.player order by s.level asc, s.timeMillis asc",
                Score.class
            )
            .getResultList();
    }

    public void replaceAll(List<Score> scores) {
        executeInTransaction(() -> {
            entityManager.createQuery("delete from Score").executeUpdate();
            entityManager.createQuery("delete from Player").executeUpdate();

            Map<String, Player> playersByName = new HashMap<>();
            for (Score score : scores) {
                String playerName = score.getPlayerName();
                Player player = playersByName.get(playerName);
                if (player == null) {
                    player = new Player(playerName);
                    entityManager.persist(player);
                    playersByName.put(playerName, player);
                }

                entityManager.persist(new Score(score.getLevel(), score.isUnlocked(), score.getTime(), player));
            }
        });
    }

    private void executeInTransaction(Runnable work) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            work.run();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public void close() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}

