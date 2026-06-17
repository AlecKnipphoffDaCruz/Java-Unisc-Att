package service;

import model.bet.Bet;
import model.team.Player;
import model.game.PostGame;
import model.game.Score;

import java.util.List;

/**
 * Pontua os gols de jogadores acertados.
 * A pontuação por gol é multiplicada pelo PESO DA POSIÇÃO:
 * acertar gol de uma posição menos provável (ex.: zagueiro) vale mais.
 */
public class ScorerRule implements ScoringRule {
    @Override
    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        if (bet.getPredictedScores() == null) {
            return 0;
        }
        int points = 0;
        for (Score predicted : bet.getPredictedScores()) {
            int realQuantity = goalsByPlayer(result.getScores(), predicted.getPlayer());
            int matched = Math.min(predicted.getQuantity(), realQuantity);
            if (matched > 0) {
                points += matched * config.getScorerBasePoints() * predicted.getPlayer().getPosition().getWeight();
            }
        }
        return points;
    }

    private int goalsByPlayer(List<Score> scores, Player player) {
        if (scores == null || player == null) {
            return 0;
        }
        int sum = 0;
        for (Score s : scores) {
            if (s.getPlayer() != null && s.getPlayer().getName().equals(player.getName())) {
                sum += s.getQuantity();
            }
        }
        return sum;
    }
}
