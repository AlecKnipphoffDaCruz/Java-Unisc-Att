package model;

/** Pontua se o participante acertou a seleção vencedora (ou o empate). */
public class WinnerRule implements ScoringRule {
    @Override
    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        Team predicted = bet.winner;
        Team actual = result.winner;

        // empate: ambos null
        if (predicted == null && actual == null) {
            return config.winnerPoints;
        }
        if (predicted != null && actual != null && predicted.id.equals(actual.id)) {
            return config.winnerPoints;
        }
        return 0;
    }
}
