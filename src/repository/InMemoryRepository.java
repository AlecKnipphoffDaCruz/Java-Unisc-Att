package repository;

import model.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementação em memória do Repository. [POLIMORFISMO via interface] */
public class InMemoryRepository<T extends Identifiable> implements Repository<T> {
    private final List<T> items = new ArrayList<>();

    @Override
    public T save(T entity) {
        items.add(entity);
        return entity;
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(items);
    }

    @Override
    public Optional<T> findById(Long id) {
        for (T item : items) {
            if (item.getId().equals(id)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}
