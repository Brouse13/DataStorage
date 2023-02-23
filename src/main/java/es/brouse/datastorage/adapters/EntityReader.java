package es.brouse.datastorage.adapters;

import es.brouse.datastorage.reflexion.Clazz;

import java.util.Map;
import java.util.Optional;

public class EntityReader {
    private final Map<String, Object> object;

    public EntityReader(Map<String, Object> object) {
        this.object = object;
    }

    public String getString(String key) {
        return getTyped(key, String.class);
    }

    public Integer getInteger(String key) {
        return getTyped(key, Integer.class);
    }

    public Double getDouble(String key) {
        return getTyped(key, Double.class);
    }

    public Float getFloat(String key) {
        return getTyped(key, Float.class);
    }

    public Long getLong(String key) {
        return getTyped(key, Long.class);
    }

    public Byte getByte(String key) {
        return getTyped(key, Byte.class);
    }

    private <T> T getTyped(String key, Class<T> tClass) {
        Optional<T> cast = Clazz.getClassManager(tClass).cast(object.get(key));
        if (!cast.isPresent()) {
            throw new ClassCastException("Te given object wasn't a " + tClass.getSimpleName());
        }

        return cast.get();
    }
}
