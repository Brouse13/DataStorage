package es.brouse.datastorage.storage;

import com.mongodb.client.model.Filters;
import es.brouse.datastorage.entity.WrappedEntity;
import es.brouse.datastorage.entity.WrappedField;
import es.brouse.datastorage.exception.ReflexionException;
import es.brouse.datastorage.exception.StorageException;
import es.brouse.datastorage.reflexion.ClassReflexion;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoHelper<T> {
    private final WrappedEntity<T> entity;

    public MongoHelper(WrappedEntity<T> entity) {
        this.entity = entity;
    }

    /**
     * Get the document associated to the {@param object}.
     *
     * @param object object to parse
     * @return the associated document
     * @throws StorageException if any exception happens during storage
     */
    public Document getDocument(T object) throws StorageException {
        WrappedField identifier = entity.getIdentifierField();
        Document dbObject = new Document();

        for (WrappedField value : entity.getFields().values()) {
            Object val = value.getField().getValue(object);

            if ((value.isNotNull() || value.getName().equals(identifier.getName())) && val == null) {
                throw new StorageException("Null element on not null context found: " + value.getName());
            }

            dbObject.put(value.getName(), val);
        }

        return dbObject;
    }

    /**
     * Get the identifier associated to the given object on a
     * {@link Bson} object.
     *
     * @param object object to parse
     * @return the associated identifier
     */
    public Bson getIdentifier(T object) {
        WrappedField identifier = entity.getIdentifierField();
        return Filters.eq(identifier.getName(), identifier.getField().getValue(object));
    }

    /**
     * Get the entity from the given mongo cursor. This is given through
     * a {@link Document}.
     *
     * @param cursor cursor to the document
     * @param classManager class reflexion
     * @return the read entity
     * @throws StorageException if any exception happens during storage
     */
    public T getEntity(final Document cursor, final ClassReflexion<T> classManager) throws StorageException {
        Object[] objects = entity.getFields().values().stream()
                .map(field -> cursor.get(field.getName(), field.getClazz()))
                .toArray();
        try {
            return classManager.instance(objects);
        } catch (ReflexionException e) {
            throw new StorageException(e);
        }
    }
}
