package model;

import model.enums.GameStatus;

public class Game {
    Long id;
    Team teamA;
    Team teamB;
    GameStatus status;

    public Game(Long id, Team teamA, Team teamB) {
        this.id = id;
        this.teamA = teamA;
        this.teamB = teamB;
        this.status = GameStatus.OPEN;
    }

    @Override
    public String toString() {
        return "Game " + id + ": " + teamA.name + " x " + teamB.name + " [" + status + "]";
    }
}
