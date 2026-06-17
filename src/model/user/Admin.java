package model.user;

/** Administrador: cadastra seleções, jogos e lança os resultados reais. */
public class Admin extends User {

    public Admin(Long id, String name) {
        super(id, name);
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}
