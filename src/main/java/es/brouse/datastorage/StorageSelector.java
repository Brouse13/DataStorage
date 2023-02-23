package es.brouse.datastorage;

import com.google.common.collect.Maps;
import es.brouse.datastorage.exception.ReflexionException;
import es.brouse.datastorage.exception.StorageException;
import es.brouse.datastorage.reflexion.ClassReflexion;
import es.brouse.datastorage.reflexion.Clazz;
import es.brouse.datastorage.storage.operations.MongoStorage;
import es.brouse.datastorage.storage.operations.MySQLStorage;
import es.brouse.datastorage.storage.operations.Storage;

import java.util.Map;

public class StorageSelector {
    private static final Map<String, Class<? extends Storage>> storages = Maps.newHashMap();

    //Add the default serializers
    static {
        storages.put("mysql", MySQLStorage.class);
        storages.put("mongodb", MongoStorage.class);
    }
    private String active = "mysql";

    /**
     * Set the active serializer from the stored serializers.
     *
     * @param name serializer name
     * @return the operation status
     */
    public boolean setActive(String name) {
        if (storages.containsKey(name)) {
            active = name;
            return true;
        }
        return false;
    }

    /**
     * Register a new serializer if is not present.
     *
     * @param name serializer name
     * @param clazz serializer class
     * @return the operation status
     */
    public boolean register(String name, Class<? extends Storage> clazz) {
        if (storages.containsKey(name) || storages.containsValue(clazz)) return false;

        storages.put(name, clazz);

        return setActive(name);
    }

    /**
     * Get the instance of the available serializer for the given.
     *
     * {@param entityClass}.
     * @param entityClass class use on the serializer
     * @return the correct serializer instance
     * @param <T> serializable entity class
     * @throws StorageException if the serializer could not be instanced
     */
    public <T> Storage<T> getActiveStorage(Class<T> entityClass) throws StorageException {
        Class<? extends Storage> clazz = storages.get(active);

        ClassReflexion<? extends Storage> classManager = Clazz.getClassManager(clazz);

        try {
            return classManager.instance(entityClass);
        } catch (ReflexionException e) {
            throw new StorageException("Unable to parse storage " + active, e);
        }
    }
}
