package service;

import model.user.Participant;

import java.util.ArrayList;
import java.util.List;

/** Gera o ranking ordenado por pontuação total (maior primeiro). */
public class RankingService {

    public List<RankingEntry> ranking(List<Participant> participants) {
        List<RankingEntry> entries = new ArrayList<>();
        for (Participant p : participants) {
            entries.add(new RankingEntry(p, p.getTotalPoints()));
        }
        entries.sort((a, b) -> Integer.compare(b.getTotalPoints(), a.getTotalPoints()));
        return entries;
    }
}
