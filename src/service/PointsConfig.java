package service;

/**
 * Valores de pontuação definidos pelo grupo.
 * O construtor sem argumentos usa valores padrão.
 */
public class PointsConfig {
    private final int winnerPoints;       // acertar a seleção vencedora
    private final int teamGoalsPoints;    // acertar o nº de gols de UMA das equipes
    private final int exactScorePoints;   // cravar o placar completo
    private final int scorerBasePoints;   // base por gol de jogador (x peso da posição)

    public PointsConfig(int winnerPoints, int teamGoalsPoints, int exactScorePoints, int scorerBasePoints) {
        this.winnerPoints = winnerPoints;
        this.teamGoalsPoints = teamGoalsPoints;
        this.exactScorePoints = exactScorePoints;
        this.scorerBasePoints = scorerBasePoints;
    }

    public PointsConfig() {
        this(5, 3, 10, 2);
    }

    public int getWinnerPoints() {
        return winnerPoints;
    }

    public int getTeamGoalsPoints() {
        return teamGoalsPoints;
    }

    public int getExactScorePoints() {
        return exactScorePoints;
    }

    public int getScorerBasePoints() {
        return scorerBasePoints;
    }
}
