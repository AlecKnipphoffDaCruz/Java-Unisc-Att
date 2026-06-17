package model;

import java.util.List;

/** Resultado real de um jogo, lançado pelo admin. */
public class PostGame {
    private final Game game;
    private final Team winner;        // null em caso de empate
    private final List<Score> scores; // goleadores reais

    public PostGame(Game game, Team winner, List<Score> scores) {
        this.game = game;
        this.winner = winner;
        this.scores = scores;
    }

    public Game getGame() {
        return game;
    }

    public Team getWinner() {
        return winner;
    }

    public List<Score> getScores() {
        return scores;
    }

    @Override
    public String toString() {
        return "Resultado " + game + " -> vencedor: " + (winner == null ? "Empate" : winner.getName());
    }
}
