package es.brouse.datastorage.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import es.brouse.datastorage.annotations.Entity;
import es.brouse.datastorage.annotations.EntityField;
import es.brouse.datastorage.annotations.EntityIdentifier;
import es.brouse.datastorage.exception.ReflexionException;
import es.brouse.datastorage.reflexion.ClassReflexion;
import es.brouse.datastorage.reflexion.Clazz;
import es.brouse.datastorage.reflexion.FieldReflexion;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EntityParser {
    private static final Set<Class<?>> VALID_FIELDS = ImmutableSet.of(
            String.class, byte.class, Byte.class, float.class, Float.class,
            short.class, Short.class, double.class, Double.class,
            long.class, Long.class, int.class, Integer.class);

    private static final Set<Class<?>> VALID_LINKED_FIELDS = ImmutableSet.of(Set.class, List.class);
    public static <T> WrappedEntity<T> parse(Class<T> clazz) throws ReflexionException {
        //Operation vars
        final ClassReflexion<?> classManager = Clazz.getClassManager(clazz);

        //Tmp vars
        Optional<Entity> entity = Clazz.getAnnotationManager(clazz).getAnnotation(Entity.class);
        Map<String, WrappedField> fieldMap = Maps.newHashMap();
        Optional<WrappedField> identifierField = Optional.empty();

        //Check if the clazz is an entity
        if (!entity.isPresent())
            throw new ReflexionException("Class " + classManager.getName() + " is not an entity");

        //Loop thought all the declared fields on the class
        for (Field field : classManager.getFields()) {
            Optional<WrappedField> parsedField = getField(field);

            //Skip non-valid fields (EntityField class not found / Not valid type)
            if (!parsedField.isPresent())
                continue;

            //If is annotated with EntityIdentifier store it
            if (Clazz.getAnnotationManager(field).isAnnotated(EntityIdentifier.class)) {
                identifierField = parsedField;
            }

            //Add the field to the map of valid fields
            fieldMap.put(parsedField.get().getName(), parsedField.get());
        }

        //Check if we have found an identifier in the fields
        if (!identifierField.isPresent())
            throw new ReflexionException("Entity must have a EntityIdentifier field");

        //Parsing all fields
        return WrappedEntity.getBuilder(clazz)
                .name(entity.get().name().equals("UNDEFINED") ? classManager.getName() : entity.get().name())
                .identifierField(identifierField.get())
                .fields(fieldMap)
                .build();
    }

    private static Optional<WrappedField> getField(Field field) {
        FieldReflexion reflexion = Clazz.getFieldManager(field);

        Optional<EntityField> annotation = Clazz.getAnnotationManager(field).getAnnotation(EntityField.class);

        if (!annotation.isPresent() || !isValid(field))
            return Optional.empty();

        return Optional.of(WrappedField.builder()
                .name(annotation.get().name().equals("UNDEFINED") ?
                        reflexion.getName() : annotation.get().name())
                .unique(annotation.get().unique())
                .size(annotation.get().size())
                .notNull(annotation.get().notNull())
                .clazz(field.getType())
                .field(field).build());
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
