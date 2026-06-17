package model;

import model.enums.GameStatus;

public class Game implements Identifiable {
    private final Long id;
    // [AGREGAÇÃO] as seleções existem independente do jogo
    private final Team teamA;
    private final Team teamB;
    private GameStatus status;
    private PostGame result;   // null enquanto não finalizado

    public Game(Long id, Team teamA, Team teamB) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.status = GameStatus.OPEN;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public GameStatus getStatus() {
        return status;
    }

    public PostGame getResult() {
        return result;
    }

    public boolean isOpen() {
        return status == GameStatus.OPEN;
    }

    /** Lança o resultado e fecha o jogo para palpites. */
    public void finish(PostGame result) {
        this.result = result;
        this.status = GameStatus.FINISHED;
    }

    @Override
    public String toString() {
        return "Jogo " + id + ": " + teamA.getName() + " x " + teamB.getName() + " [" + status + "]";
    }
}
