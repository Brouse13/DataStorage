package es.brouse.datastorage;

import com.google.common.collect.Maps;
import es.brouse.datastorage.entity.WrappedEntity;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

public class DataStorage {
    @Getter private static final DataStorage instance = new DataStorage();
    private final StorageSelector storageSelector = new StorageSelector();

    private static final Map<Class<?>, WrappedEntity<?>> entities = Maps.newHashMap();

    /**
     * Add a new entity to the set of available serializable
     * entities.
     *
     * @param entity entity to add
     * @return if the entity was added
     */
    public boolean addEntity(WrappedEntity<?> entity) {
        return entities.putIfAbsent(entity.getClazz(), entity) == null;
    }

    /**
     * Get the entity that matches with the provided class. If is not present
     * will return an empty optional.
     *
     * @param clazz entity class
     * @return the found entity if present
     * @param <T> type of the class
     */
    public <T> Optional<WrappedEntity<T>> getEntity(Class<T> clazz) {
        if (entities.containsKey(clazz)) {
            return Optional.of((WrappedEntity<T>) entities.get(clazz));
        }
        return Optional.empty();
    }

    /**
     * Get the instance of the {@link StorageSelector} that will be
     * the responsive to get the Storage manager in each time as well
     * as to add new serialization methods.
     *
     * @return StorageSelector instance
     */
    public StorageSelector getStorageSelector() {
        return storageSelector;
    }
}
