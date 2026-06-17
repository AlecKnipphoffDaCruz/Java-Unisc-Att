package model;

import java.util.List;

/** Palpite de um participante para um jogo. */
public class Bet {
    private final Participant participant;
    private final Game game;
    private final List<Score> predictedScores;   // goleadores palpitados
    private final Team predictedWinner;           // derivado do placar palpitado
    private int points;                           // calculado após o jogo finalizar

    public Bet(Participant participant, Game game, List<Score> predictedScores, Team predictedWinner) {
        this.participant = participant;
        this.game = game;
        this.predictedScores = predictedScores;
        this.predictedWinner = predictedWinner;
    }

    public Participant getParticipant() {
        return participant;
    }

    public Game getGame() {
        return game;
    }

    public List<Score> getPredictedScores() {
        return predictedScores;
    }

    public Team getPredictedWinner() {
        return predictedWinner;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return game + " -> " + points + " ponto(s)";
    }
}
