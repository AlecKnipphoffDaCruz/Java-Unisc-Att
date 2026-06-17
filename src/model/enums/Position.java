package model.enums;

public enum Position {
    // peso MAIOR = posição menos provável de marcar = mais pontos ao acertar.
    // TODO: ajuste os valores como o grupo preferir.
    GOALKEEPER(5),
    DEFENDER(4),
    FULLBACK(3),
    MIDFIELDER(2),
    FORWARD(1);

    private final int weight;

    Position(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
