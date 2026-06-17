package model.game;

import model.team.Player;
import model.team.Team;

/**
 * Um gol (ou conjunto de gols) de um jogador num jogo.
 * Reutilizado tanto no palpite (Bet) quanto no resultado real (PostGame).
 */
public class Score {
    private final Game game;
    private final Team team;
    private final Player player;
    private final int quantity;

    public Score(Game game, Team team, Player player, int quantity) {
        this.game = game;
        this.team = team;
        this.player = player;
        this.quantity = quantity;
    }

    public Game getGame() {
        return game;
    }

    public Team getTeam() {
        return team;
    }

    public Player getPlayer() {
        return player;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return player.getName() + " (" + team.getName() + "): " + quantity + " gol(s)";
    }
}
