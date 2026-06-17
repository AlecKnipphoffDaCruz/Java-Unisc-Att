package repository;

import model.Identifiable;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de repositório. [INTERFACE + GENERICS]
 * Funciona para qualquer entidade que seja Identifiable.
 */
public interface Repository<T extends Identifiable> {
    T save(T entity);

    List<T> findAll();

    Optional<T> findById(Long id);
}
