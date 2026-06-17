package model.team;

import model.enums.Position;

public class Player {
    private final String name;
    private final Team team;
    private final Position position;

    public Player(String name, Team team, Position position) {
        this.name = name;
        this.team = team;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return name + " (" + position + ")";
    }
}
