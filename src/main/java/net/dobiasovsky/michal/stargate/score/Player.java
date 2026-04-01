package net.dobiasovsky.michal.stargate.score;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "players")
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Score> scores = new ArrayList<>();

    public Player(String name) {
        this.name = name != null ? name : "";
    }

    public void addScore(Score score) {
        if (score == null) {
            return;
        }
        score.setPlayer(this);
        scores.add(score);
    }

    public List<Score> getScores() {
        return List.copyOf(scores);
    }
}

