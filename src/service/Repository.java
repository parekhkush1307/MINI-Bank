package service;

import model.Account;
import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Lab 13 – Generic repository that stores any entity type {@code T}
 * keyed by the field annotated with {@code @Id}.
 *
 * <p>Type safety replaces the raw {@code Object}-based HashMap used in earlier labs.
 * The same class works for {@code Account}, {@code Customer}, or any future type.</p>
 */
public class Repository<T> {

    private final HashMap<String, T> store = new HashMap<>();

    // ------------------------------------------------------------------ save

    /**
     * Persists {@code entity} into the repository, using the {@code @Id}-annotated
     * field as the map key.
     *
     * @param entity the entity to save
     * @return the saved entity
     * @throws IllegalArgumentException if no {@code @Id} field is found or is not accessible
     */
    public T save(T entity) {
        String id = extractId(entity);
        store.put(id, entity);
        return entity;
    }

    /**
     * Lab 13 – Saves multiple entities at once (varargs, @SafeVarargs).
     */
    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    // ------------------------------------------------------------------ find

    /**
     * Returns the entity with the given {@code id}, or {@code null} if not found.
     */
    public T findById(String id) {
        return store.get(id);
    }

    /**
     * Returns all stored entities.
     */
    public ArrayList<T> findAll() {
        return new ArrayList<>(store.values());
    }

    // ------------------------------------------------------------------ delete

    /**
     * Removes the entity with the given {@code id}.
     * @return {@code true} if an entity was removed
     */
    public boolean delete(String id) {
        return store.remove(id) != null;
    }

    /** Returns the number of stored entities. (Lab 13 Supplementary) */
    public int count() {
        return store.size();
    }

    // ------------------------------------------------------------------ generic static utility (Lab 13)

    /**
     * Lab 13 – Generic bounded method: computes the average of an array of numbers.
     *
     * @param values array of any {@link Number} subtype
     * @param <N>    the number type (Integer, Long, Double, …)
     * @return arithmetic mean, or 0.0 if the array is empty
     */
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) return 0.0;
        double sum = 0;
        for (N v : values) sum += v.doubleValue();
        return sum / values.length;
    }

    // ------------------------------------------------------------------ internal helpers

    /** Finds the @Id-annotated field and returns its String value. */
    private String extractId(T entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object val = field.get(entity);
                        if (val == null) {
                            throw new IllegalArgumentException(
                                    "@Id field '" + field.getName() + "' is null");
                        }
                        return val.toString();
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(
                                "Cannot access @Id field: " + field.getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        throw new IllegalArgumentException(
                "No @Id-annotated field found on " + entity.getClass().getSimpleName());
    }
}
