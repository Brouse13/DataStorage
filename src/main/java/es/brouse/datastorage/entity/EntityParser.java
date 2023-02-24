package es.brouse.datastorage.entity;

import es.brouse.datastorage.annotations.Entity;
import es.brouse.datastorage.entity.field.FieldParser;
import es.brouse.datastorage.exception.MalformedEntity;
import es.brouse.datastorage.reflexion.ClassReflexion;
import es.brouse.datastorage.reflexion.Clazz;

import java.util.*;
import java.util.stream.Collectors;

public class EntityParser {
    public static <T> WrappedEntity<T> parse(Class<T> clazz) throws MalformedEntity {
        final ClassReflexion<?> classManager = Clazz.getClassManager(clazz);

        Entity entity = Clazz.getAnnotationManager(clazz).getAnnotation(Entity.class)
                .orElseThrow(() -> new MalformedEntity("Class " + classManager.getName() + " is not an entity"));

        //Map the fields to a map of <String, WrappedField> using FieldParser class
        Map<String, WrappedField> fields = classManager.getFields().stream()
                .map(FieldParser::new)
                .map(FieldParser::getField)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(WrappedField::getName, wrappedField -> wrappedField));

        //Get the first identifier or throw exception
        WrappedField identifier = fields.values().stream()
                .filter(WrappedField::isIdentifier)
                .findFirst()
                .orElseThrow(() -> new MalformedEntity("Entity must have an identifier"));

        //Construct the builder
        return WrappedEntity.getBuilder(clazz)
                .name(entity.name().equals("UNDEFINED") ? classManager.getName() : entity.name())
                .identifierField(identifier)
                .fields(fields)
                .build();
    }
}
