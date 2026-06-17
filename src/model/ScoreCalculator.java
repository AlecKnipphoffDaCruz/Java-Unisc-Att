package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Roda todas as regras de pontuação sobre um palpite e soma o resultado.
 * Adicionar/remover critérios = só mexer na lista de regras. [POLIMORFISMO]
 */
public class ScoreCalculator {
    private final List<ScoringRule> rules;

    public ScoreCalculator() {
        rules = new ArrayList<>();
        rules.add(new WinnerRule());
        rules.add(new TeamGoalsRule());
        rules.add(new ExactScoreRule());
        rules.add(new ScorerRule());
    }

    public int calculate(Bet bet, PostGame result, PointsConfig config) {
        int total = 0;
        for (ScoringRule rule : rules) {
            total += rule.calculate(bet, result, config);
        }
        return total;
    }

    /** Soma os gols de uma equipe em uma lista de Score (palpitada ou real). */
    static int goalsOf(List<Score> scores, Team team) {
        if (scores == null || team == null) {
            return 0;
        }
        int sum = 0;
        for (Score s : scores) {
            if (s.team != null && s.team.id != null && s.team.id.equals(team.id)) {
                sum += s.quantity;
            }
        }
        return sum;
    }
}
