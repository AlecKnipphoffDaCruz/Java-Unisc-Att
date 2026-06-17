package model;

/** Pontua para cada equipe cujo número de gols o participante acertou. */
public class TeamGoalsRule implements ScoringRule {
    @Override
    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        Team a = bet.game.teamA;
        Team b = bet.game.teamB;
        int points = 0;

        if (ScoreCalculator.goalsOf(bet.scoreList, a) == ScoreCalculator.goalsOf(result.scores, a)) {
            points += config.teamGoalsPoints;
        }
        if (ScoreCalculator.goalsOf(bet.scoreList, b) == ScoreCalculator.goalsOf(result.scores, b)) {
            points += config.teamGoalsPoints;
        }
        return points;
    }
}
