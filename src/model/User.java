package model;

import model.enums.Role;

public class User {
    Long id;
    String name;
    Role role;

    public User(Long id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User: " + name + " (" + role + ")";
    }
}
