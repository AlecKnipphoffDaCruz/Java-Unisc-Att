package model.team;

import model.Identifiable;

import java.util.ArrayList;
import java.util.List;

public class Team implements Identifiable {
    private final Long id;
    private final String name;
    // [COMPOSIÇÃO] os jogadores pertencem à seleção
    private final List<Player> players = new ArrayList<>();

    public Team(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    @Override
    public String toString() {
        return name;
    }
}
