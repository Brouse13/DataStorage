package es.brouse.datastorage.storage;

import com.google.common.collect.Sets;
import es.brouse.datastorage.entity.WrappedEntity;
import es.brouse.datastorage.entity.WrappedField;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MySQLHelper {
    private static final Map<Class<?>, String> CONVERTED_TYPES = Stream.of(
            new AbstractMap.SimpleEntry<>(String.class, "VARCHAR(%d)"),
            new AbstractMap.SimpleEntry<>(Byte.class, "TINYINT(%d)"),
            new AbstractMap.SimpleEntry<>(Short.class, "SMALLINT(%d)"),
            new AbstractMap.SimpleEntry<>(Double.class, "DOUBLE"),
            new AbstractMap.SimpleEntry<>(Integer.class, "INT(%d)"),
            new AbstractMap.SimpleEntry<>(Float.class, "FLOAT(%d)"),
            new AbstractMap.SimpleEntry<>(Long.class, "BIGINT(%d)"),
            new AbstractMap.SimpleEntry<>(byte.class, "TINYINT(%d)"),
            new AbstractMap.SimpleEntry<>(double.class, "DOUBLE"),
            new AbstractMap.SimpleEntry<>(short.class, "SMALLINT(%d)"),
            new AbstractMap.SimpleEntry<>(int.class, "INT(%d)"),
            new AbstractMap.SimpleEntry<>(float.class, "FLOAT(%d)"),
            new AbstractMap.SimpleEntry<>(long.class, "BIGINT(%d)")
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    /**
     * Generate the creation of the main SQL table if it doesn't exist yet.
     * It will be called the first time a {@link WrappedEntity<>} class in
     * instanced.
     *
     * @param entity entity to create its sql table
     * @return the SQL sentence to create the table
     */
    public LinkedHashSet<String> generateTable(final WrappedEntity<?> entity) {
        LinkedHashSet<String> sqls = Sets.newLinkedHashSet();
        Set<String> unique = Sets.newHashSet();

        String identifier = entity.getIdentifierField().getName();

        sqls.add("CREATE TABLE IF NOT EXISTS " + entity.getName() + " (" +
                //Add each fields following the pattern <field_name field_type>
                entity.getFields().values().stream()
                        .map(field -> {
                            boolean isIdentifier = field.getName().equals(identifier);

                            String sql = field.getName() + " ";
                            sql += String.format(CONVERTED_TYPES.get(field.getClazz()), field.getSize());

                            //Add not null to identifiers and present fields
                            sql += field.isNotNull() | isIdentifier ?
                                    " NOT NULL" : "";

                            //Add unique to identifiers to the set
                            if (field.isUnique() | isIdentifier) {
                                unique.add(field.getName());
                            }

                            return sql;
                        })
                        .collect(Collectors.joining(", ")) + ");");

        sqls.add("ALTER TABLE " + entity.getName() + " ADD CONSTRAINT " +
                "PK_"  + entity.getName() + "_" + identifier  +
                " PRIMARY KEY (" + identifier + ");");

        sqls.add("ALTER TABLE " + entity.getName() + " ADD CONSTRAINT " +
                "UC_"  + entity.getName() + "_" + identifier +
                " UNIQUE (" + String.join(", ", unique) + ");");
        return sqls;
    }

    /**
     * Generate the creation of the SQL INSERT sentence
     * looking for the content on the given {@param object}.
     *
     * @param entity entity to create the insert
     * @param object instanced object to fill content with
     * @return the SQL sentence to INSERT an entity
     */
    public String generateInsert(final WrappedEntity<?> entity, final Object object) {
        return "INSERT INTO " + entity.getName() + " (" +
                String.join(", ", entity.getFields().keySet()) +
                ") VALUES (" + joinObjectValues(entity, object) + ");";
    }


    /**
     * Generate the creation of the SQL UPDATE sentence
     * looking for the content on the given {@param object}
     *
     * @param entity entity to create the update
     * @param object instanced object to fill content with
     * @return the SQL sentence to UPDATE the entity
     */
    public String generateUpdate(final WrappedEntity<?> entity, final Object object) {
        WrappedField identifier = entity.getIdentifierField();

        return "UPDATE " + entity.getName() + " SET " +
                entity.getFields().values().stream()
                        .map(field ->  {
                            return field.getName() + "=" +
                                    objectToString(field.getClazz(), field.getField().getValue(object));
                        })
                        .collect(Collectors.joining(", ")) +
                " WHERE " +
                identifier.getName() + "=" +
                objectToString(identifier.getClazz(), identifier.getField().getValue(object)) + ";";
    }

    /**
     * Generate the creation of the SQL DELETE sentence
     * looking for the content on the given {@param object}
     *
     * @param entity entity to create the update
     * @param identifier identifier to delete the entity
     * @return the SQL sentence to DELETE the entity
     */
    public String generateDelete(final WrappedEntity<?> entity, final String identifier) {
        return "DELETE FROM " + entity.getName() +
                " WHERE " + entity.getIdentifierField().getName() +
                "='" + identifier + "';";
    }

    /**
     * Generate the creation of the SQL SELECT sentence
     * looking for the content on the given {@param object}
     *
     * @param entity entity to create the update
     * @param identifier identifier to delete the entity
     * @return the SQL sentence to SELECT the entity
     */
    public String generateSelect(final WrappedEntity<?> entity, final String identifier) {
        return "SELECT * FROM " + entity.getName() +
                " WHERE " + entity.getIdentifierField().getName() +
                "='" + identifier + "';";
    }

    /**
     * Generate the creation of the SQL SELECT sentence
     * looking for the content on the given {@param object}
     *
     * @param entity entity to create the update
     * @return the SQL sentence to SELECT the entity
     */
    public String generateSelect(final WrappedEntity<?> entity, int from, int to) {
        return "SELECT * FROM " + entity.getName() +
                " LIMIT " + from + ", " + to + ";";
    }

    /**
     * Join the values of the all the params of the instanced
     * {@param object}.
     *
     * @param entity entity to get the fields from
     * @param object instanced object to fill content with
     * @return the String that contains all the objects params
     */
    private String joinObjectValues(final WrappedEntity<?> entity, Object object) {
        return entity.getFields().values().stream()
                .map(WrappedField::getField)
                .map(field ->  objectToString(field.getType(), field.getValue(object)))
                .collect(Collectors.joining(", "));
    }

    /**
     * Transform the given {@param object} into a SQL
     * String to be used on the queries.
     *
     * @param clazz clazz of the object
     * @param value value to parse
     * @return the String form of the given object
     */
    private String objectToString(Class<?> clazz, Object value) {
        String stringValue = String.valueOf(value);

        if (clazz.equals(String.class)) {
            return "'" + value.toString() + "'";
        } else if (clazz.equals(Byte.class)) {
            return Integer.toHexString(Integer.parseInt(stringValue));
        }
        return stringValue;
    }
}
