package model;

/**
 * Usuário do sistema. Classe ABSTRATA — não existe "usuário genérico",
 * todo usuário é um Admin ou um Participant. [HERANÇA]
 */
public abstract class User implements Identifiable {
    private final Long id;
    private final String name;

    protected User(Long id, String name) {
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

    /** Cada subtipo define seu próprio papel. [POLIMORFISMO] */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + ": " + name + " (#" + id + ")";
    }
}
