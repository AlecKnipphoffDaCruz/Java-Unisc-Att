package model;

/**
 * Valores de pontuação definidos pelo grupo.
 * O construtor sem argumentos usa valores padrão; troque como quiser.
 */
public class PointsConfig {
    int winnerPoints;       // acertar a seleção vencedora
    int teamGoalsPoints;    // acertar o nº de gols de UMA das equipes
    int exactScorePoints;   // cravar o placar completo
    int scorerBasePoints;   // base por gol de jogador (multiplicada pelo peso da posição)

    public PointsConfig(int winnerPoints, int teamGoalsPoints, int exactScorePoints, int scorerBasePoints) {
        this.winnerPoints = winnerPoints;
        this.teamGoalsPoints = teamGoalsPoints;
        this.exactScorePoints = exactScorePoints;
        this.scorerBasePoints = scorerBasePoints;
    }

    public PointsConfig() {
        this(5, 3, 10, 2);
    }
}
