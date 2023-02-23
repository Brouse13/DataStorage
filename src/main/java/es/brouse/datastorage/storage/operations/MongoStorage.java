package es.brouse.datastorage.storage.operations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import es.brouse.datastorage.DataStorage;
import es.brouse.datastorage.entity.WrappedEntity;
import es.brouse.datastorage.entity.WrappedField;
import es.brouse.datastorage.exception.StorageException;
import es.brouse.datastorage.reflexion.Clazz;
import es.brouse.datastorage.storage.MongoHelper;
import es.brouse.datastorage.storage.objects.MongoDB;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MongoStorage<T> implements Storage<T> {
    private final MongoHelper<T> helper;
    private final Class<T> clazz;
    private final WrappedEntity<T> entity;

    private final MongoCollection<Document> collection;

    public MongoStorage(Class<T> clazz) throws StorageException {
        this.clazz = clazz;
        this.entity = DataStorage.getInstance().getEntity(clazz)
                .orElseThrow(() -> new StorageException("Unparsed entity: " + clazz.getSimpleName()));

        this.helper = new MongoHelper<>(entity);

        //Init the collection from MongoDB
        collection = MongoDB.getInstance().getDatabase(entity);
    }

    @Override
    public int insert(Collection<T> objects) throws StorageException {
        int count = 0;

        //Loop all the objects creating a Document for each object
        for (T object : objects) {
            try {
                //Intercept duplicate entry exception
                collection.insertOne(helper.getDocument(object));
                count++;
            }catch (MongoWriteException e) {
                throw new StorageException("Duplicate key: " + entity.getIdentifierField().getName());
            }
        }

        return count;
    }

    @Override
    public Optional<Collection<T>> read(Collection<String> identifiers) throws StorageException {
        Collection<T> readEntities = Sets.newLinkedHashSet();

        //Map each identifier to a DBObject
        Set<BasicDBObject> collect = identifiers.stream()
                .map(identifier -> new BasicDBObject(entity.getIdentifierField().getName(), identifier))
                .collect(Collectors.toSet());

        //Loop all the mapped objects
        for (BasicDBObject dbObject : collect) {
            MongoCursor<Document> cursor = collection.find(dbObject).cursor();

            while (cursor.hasNext()) {
                readEntities.add(helper.getEntity(cursor.next(), Clazz.getClassManager(clazz)));
            }
        }

        return Optional.of(readEntities);
    }

    @Override
    public Optional<Collection<T>> read(int from, int to) throws StorageException {
        if (from < 0 || from >= to) return Optional.empty();

        Collection<T> readEntities = Sets.newLinkedHashSet();
        int index = 0;

        MongoCursor<Document> cursor = collection.find().cursor();

        //Move cursor to 'from' and read to 'to'
        while (cursor.hasNext() && index < to) {
            Document next = cursor.next();

            if (index >= from - 1)
                readEntities.add(helper.getEntity(next, Clazz.getClassManager(clazz)));
            index++;
        }

        return Optional.of(readEntities);
    }

    @Override
    public int update(Collection<T> objects) throws StorageException {
        int count = 0;

        //Create the update document
        for (T o : objects) {
            List<Bson> updates = Lists.newArrayList();
            WrappedField identifier = entity.getIdentifierField();

            for (WrappedField value : entity.getFields().values()) {
                //Skip identifier
                if (identifier.getName().equals(value.getName())) continue;

                Object val = value.getField().getValue(o);

                //Check null
                if (value.isNotNull() && val == null)
                    throw new StorageException("Null element on not null context found: " + value.getName());

                updates.add(Updates.set(value.getName(), val));
            }

            //Update the collection
            if (collection.findOneAndUpdate(helper.getIdentifier(o), Updates.combine(updates)) != null)
                count++;
        }

        return count;
    }

    @Override
    public int delete(Collection<String> identifiers) throws StorageException {
        if (identifiers.isEmpty()) return 0;

        identifiers.forEach(identifier -> collection.deleteOne(Filters.eq(
                entity.getIdentifierField().getName(), identifier)));

        //Make a read from the database to count and check
        return read(identifiers).map(Collection::size).orElse(0);
    }
}
