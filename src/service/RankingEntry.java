package service;

import model.user.Participant;

/** Uma linha do ranking: participante + sua pontuação total. */
public class RankingEntry {
    private final Participant participant;
    private final int totalPoints;

    public RankingEntry(Participant participant, int totalPoints) {
        this.participant = participant;
        this.totalPoints = totalPoints;
    }

    public Participant getParticipant() {
        return participant;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    @Override
    public String toString() {
        return participant.getName() + " - " + totalPoints + " pts";
    }
}
