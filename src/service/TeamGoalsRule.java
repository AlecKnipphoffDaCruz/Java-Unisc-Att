package service;

import model.bet.Bet;
import model.game.PostGame;
import model.team.Team;

/** Pontua para cada equipe cujo número de gols o participante acertou. */
public class TeamGoalsRule implements ScoringRule {
    @Override
    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        Team a = bet.getGame().getTeamA();
        Team b = bet.getGame().getTeamB();
        int points = 0;

        if (ScoreCalculator.goalsOf(bet.getPredictedScores(), a) == ScoreCalculator.goalsOf(result.getScores(), a)) {
            points += config.getTeamGoalsPoints();
        }
        if (ScoreCalculator.goalsOf(bet.getPredictedScores(), b) == ScoreCalculator.goalsOf(result.getScores(), b)) {
            points += config.getTeamGoalsPoints();
        }
        return points;
    }
}
