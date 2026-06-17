package model;

/** Contrato para entidades que possuem identificador. Usado pelo Repository<T>. */
public interface Identifiable {
    Long getId();
}
