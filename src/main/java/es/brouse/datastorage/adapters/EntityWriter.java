package es.brouse.datastorage.adapters;

import com.google.common.collect.Maps;

import java.util.Map;

public class EntityWriter {
    private final Map<String, Object> object = Maps.newHashMap();

    public EntityWriter writeString(String key, String arg) {
        object.put(key, arg);
        return this;
    }

    public EntityWriter writeInteger(String key, Integer arg) {
        object.put(key, arg);
        return this;
    }

    public EntityWriter writeDouble(String key, Double arg) {
        object.put(key, arg);
        return this;
    }

    public EntityWriter writeFloat(String key, Float arg) {
        object.put(key, arg);
        return this;
    }

    public EntityWriter writeLong(String key, Long arg) {
        object.put(key, arg);
        return this;
    }

    public EntityWriter writeByte(String key, Byte arg) {
        object.put(key, arg);
        return this;
    }

    public Map<String, Object> toMap() {
        return object;
    }
}
