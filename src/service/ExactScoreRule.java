package service;

import model.Bet;
import model.PostGame;
import model.Team;

/** Pontua se o participante cravou o placar completo (gols das duas equipes). */
public class ExactScoreRule implements ScoringRule {
    @Override
    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        Team a = bet.getGame().getTeamA();
        Team b = bet.getGame().getTeamB();

        boolean matchA = ScoreCalculator.goalsOf(bet.getPredictedScores(), a) == ScoreCalculator.goalsOf(result.getScores(), a);
        boolean matchB = ScoreCalculator.goalsOf(bet.getPredictedScores(), b) == ScoreCalculator.goalsOf(result.getScores(), b);

        return (matchA && matchB) ? config.getExactScorePoints() : 0;
    }
}
