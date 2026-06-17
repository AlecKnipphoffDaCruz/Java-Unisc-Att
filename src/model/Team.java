package model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    Long id;
    String name;
    List<Player> playersList;

    public Team(Long id, String name) {
        this.id = id;
        this.name = name;
        this.playersList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }
}
