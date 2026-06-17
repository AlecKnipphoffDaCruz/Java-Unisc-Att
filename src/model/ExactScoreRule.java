package model;

/** Pontua se o participante cravou o placar completo (gols das duas equipes). */
public class ExactScoreRule implements ScoringRule {
    @Override
    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        Team a = bet.game.teamA;
        Team b = bet.game.teamB;

        boolean matchA = ScoreCalculator.goalsOf(bet.scoreList, a) == ScoreCalculator.goalsOf(result.scores, a);
        boolean matchB = ScoreCalculator.goalsOf(bet.scoreList, b) == ScoreCalculator.goalsOf(result.scores, b);

        return (matchA && matchB) ? config.exactScorePoints : 0;
    }
}
