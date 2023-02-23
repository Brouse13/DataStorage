package es.brouse.datastorage.entity;

import es.brouse.datastorage.reflexion.Clazz;
import es.brouse.datastorage.reflexion.FieldReflexion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;

@Builder
@AllArgsConstructor
public class WrappedField {
    @Getter private final String name;

    @Getter private final boolean unique;

    @Getter private final boolean notNull;

    @Getter private int size;
    private final Field field;

    @Getter private final Class<?> clazz;

    public FieldReflexion getField() {
        return Clazz.getFieldManager(field);
    }

    @Override
    public String toString() {
        return "WrappedField{" +
                "name='" + name + '\'' +
                ", unique=" + unique +
                ", field=" + field +
                ", clazz=" + clazz +
                '}';
    }
}
