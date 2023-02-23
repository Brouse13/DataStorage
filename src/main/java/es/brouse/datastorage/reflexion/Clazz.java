package es.brouse.datastorage.reflexion;

import java.lang.reflect.Field;

public final class Clazz {
    /**
     * Get the {@link ClassReflexion} instance for the given {@param clazz}
     * @param clazz clazz to use on reflexion
     * @return the instance of the ClassReflexion
     * @param <T> clazz type
     */
    public static <T> ClassReflexion<T> getClassManager(Class<T> clazz) {
        return new ClassReflexion<>(clazz);
    }

    /**
     * Get the {@link FieldReflexion} instance for the given {@param field}
     * @param field field to use on the reflexion
     * @return the instance of the FieldReflexion
     */
    public static FieldReflexion getFieldManager(Field field) {
        return new FieldReflexion(field);
    }

    /**
     * Get the {@link AnnotationReflexion} instance for the given {@param clazz}
     * @param clazz clazz to use on the reflexion
     * @return the instance of the AnnotationReflexion
     */
    public static AnnotationReflexion getAnnotationManager(Class<?> clazz) {
        return new AnnotationReflexion(clazz);
    }

    /**
     * Get the {@link AnnotationReflexion} instance for the given {@param field}
     * @param field field to use on the reflexion
     * @return the instance of the AnnotationReflexion
     */
    public static AnnotationReflexion getAnnotationManager(Field field) {
        return new AnnotationReflexion(field);
    }
}
