package es.brouse.datastorage.entity.field;

import com.google.common.collect.ImmutableSet;
import es.brouse.datastorage.annotations.EntityField;
import es.brouse.datastorage.annotations.EntityIdentifier;
import es.brouse.datastorage.entity.WrappedField;
import es.brouse.datastorage.reflexion.AnnotationReflexion;
import es.brouse.datastorage.reflexion.Clazz;
import es.brouse.datastorage.reflexion.FieldReflexion;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

public class FieldParser {
    public static final Set<Class<?>> VALID_FIELDS = ImmutableSet.of(
            String.class, byte.class, Byte.class, float.class, Float.class,
            short.class, Short.class, double.class, Double.class,
            long.class, Long.class, int.class, Integer.class);

    private final FieldReflexion fieldReflexion;
    private final AnnotationReflexion annotationManager;
    private final Field field;

    public FieldParser(Field field) {
        this.field = field;
        this.fieldReflexion = Clazz.getFieldManager(field);
        this.annotationManager = Clazz.getAnnotationManager(field);
    }

    public Optional<WrappedField> getField() {
        EntityField entity = annotationManager.getAnnotation(EntityField.class).orElse(null);

        if (entity == null || !isValid(field)) return Optional.empty();

        Optional<EntityIdentifier> identifier = annotationManager.getAnnotation(EntityIdentifier.class);

        WrappedField wrappedField = WrappedField.builder()
                .name(entity.name().equals("UNDEFINED") ? fieldReflexion.getName() : entity.name())
                .unique(entity.unique())
                .size(entity.size())
                .notNull(entity.notNull())
                .clazz(field.getType())
                .identifier(identifier.isPresent())
                .field(field).build();

        return Optional.of(wrappedField);
    }

    private static boolean isValid(Field field) {
        FieldReflexion fieldReflexion = Clazz.getFieldManager(field);

        for (Class<?> validField : VALID_FIELDS) {
            if (fieldReflexion.checkType(validField))
                return true;
        }
        return false;
    }
}
