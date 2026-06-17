package model;

import java.util.List;

/**
 * Pontua os gols de jogadores acertados.
 * A pontuação por gol é multiplicada pelo PESO DA POSIÇÃO:
 * acertar gol de uma posição menos provável (ex.: zagueiro) vale mais.
 */
public class ScorerRule implements ScoringRule {
    @Override
    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        if (bet.scoreList == null) {
            return 0;
        }
        int points = 0;
        for (Score predicted : bet.scoreList) {
            int realQuantity = goalsByPlayer(result.scores, predicted.player);
            int matched = Math.min(predicted.quantity, realQuantity);
            if (matched > 0) {
                points += matched * config.scorerBasePoints * predicted.player.position.getWeight();
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
            if (s.player != null && s.player.name.equals(player.name)) {
                sum += s.quantity;
            }
        }
        return sum;
    }
}
