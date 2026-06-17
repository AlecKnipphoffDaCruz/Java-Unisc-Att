package service;

import model.Bet;
import model.PostGame;

/**
 * Interface de uma regra de pontuação. [INTERFACE]
 * Cada critério do desafio é uma implementação diferente. [POLIMORFISMO]
 */
public interface ScoringRule {
    int calculate(Bet bet, PostGame result, PointsConfig config);
}
