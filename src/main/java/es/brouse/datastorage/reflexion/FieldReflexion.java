package es.brouse.datastorage.reflexion;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class FieldReflexion {
    @Getter private final Field field;

    /**
     * FieldReflexion constructor to create a new instance.
     *
     * @param field field to bind
     */
    public FieldReflexion(Field field) {
        this.field = field;
    }

    /**
     * Execute the {@param action} action on the given field.
     * It will not depend on its access modifier.
     *
     * @param action action to perform
     */
    public void fieldAccessor(Consumer<Field> action) {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        action.accept(field);
        field.setAccessible(accessible);
    }

    /**
     * Get the type of the field.
     *
     * @return field type
     */
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * Check if the field class is equals to the {@param type}.
     *
     * @param type class to check
     * @return the field class comparation
     */
    public boolean checkType(Class<?> type) {
        return field.getType().isAssignableFrom(type);
    }

    /**
     * Get the content of the field without depending
     * on its access.
     *
     * @param object instance of the class of the field
     * @return the field content value
     */
    public Object getValue(Object object) {
        AtomicReference<Object> returnObject = new AtomicReference<>();
        fieldAccessor(field1 -> {
            try {
                returnObject.set(field1.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        return returnObject.get();
    }

    /**
     * Set  the content of the field without depending
     * on its access.
     *
     * @param object instance of the class of the field
     * @param value value to set
     */
    public void setValue(Object object, Object value) {
        fieldAccessor(field -> {
            try {
                field.set(object, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Get the name of the field.
     *
     * @return the field name
     */
    public String getName() {
        return field.getName();
    }
}
