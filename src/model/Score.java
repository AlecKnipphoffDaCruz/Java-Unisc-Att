package model;

public class Score {
    Game game;
    Team team;
    Player player;
    int quantity;   // quantos gols esse jogador fez (ou que se palpita que fará)

    public Score(Game game, Team team, Player player, int quantity) {
        this.game = game;
        this.team = team;
        this.player = player;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return player.name + " (" + team.name + "): " + quantity + " gol(s)";
    }
}
