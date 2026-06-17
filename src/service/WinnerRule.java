package service;

import model.bet.Bet;
import model.game.PostGame;
import model.team.Team;

/** Pontua se o participante acertou a seleção vencedora (ou o empate). */
public class WinnerRule implements ScoringRule {
    @Override
    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        Team predicted = bet.getPredictedWinner();
        Team actual = result.getWinner();

        if (predicted == null && actual == null) {
            return config.getWinnerPoints();
        }
        if (predicted != null && actual != null && predicted.getId().equals(actual.getId())) {
            return config.getWinnerPoints();
        }
        return 0;
    }
}
