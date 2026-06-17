package model;

import model.enums.Position;

public class Player {
    String name;
    Team team;
    Position position;

    @Override
    public String toString() {
        return "Player: " + name + " " + position;
    }
}
