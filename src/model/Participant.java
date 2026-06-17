package model;

import java.util.ArrayList;
import java.util.List;

/** Participante do bolão: dá palpites e acumula pontos. */
public class Participant extends User {

    // [AGREGAÇÃO] o participante mantém seus palpites
    private final List<Bet> bets = new ArrayList<>();

    public Participant(Long id, String name) {
        super(id, name);
    }

    @Override
    public String getRole() {
        return "Participante";
    }

    public List<Bet> getBets() {
        return bets;
    }

    public void addBet(Bet bet) {
        bets.add(bet);
    }

    /** Pontuação total = soma dos pontos de todos os palpites. */
    public int getTotalPoints() {
        int total = 0;
        for (Bet bet : bets) {
            total += bet.getPoints();
        }
        return total;
    }
}
