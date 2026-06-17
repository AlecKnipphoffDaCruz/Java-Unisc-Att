package model;

import java.util.List;

public class PostGame {
    Game game;
    Team winner;          // pode ser null em caso de empate
    List<Score> scores;

    public PostGame(Game game, Team winner, List<Score> scores) {
        this.game = game;
        this.winner = winner;
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "Resultado " + game + " -> vencedor: "
                + (winner == null ? "Empate" : winner.name);
    }
}
