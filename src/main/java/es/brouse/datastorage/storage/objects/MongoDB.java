package es.brouse.datastorage.storage.objects;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import es.brouse.datastorage.entity.WrappedEntity;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDB {
    @Getter private static MongoDB instance = new MongoDB();

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    static {
        if (mongoClient == null) {
            mongoClient = new MongoClient("localhost", 27017);
            database = mongoClient.getDatabase("minecraft");
        }
    }

    //Private constructor to force using class instance pattern
    private MongoDB() {}

    public MongoCollection<Document> getDatabase(final WrappedEntity<?> entity) {
        boolean found = false;

        //Find if the collection already exists
        for (String name : database.listCollectionNames()) {
            if (name.equals(entity.getName())) {
                found = true;
                break;
            }
        }

        MongoCollection<Document> collection = database.getCollection(entity.getName());

        if (!found) {
            database.createCollection(entity.getName());
            collection.createIndex(Filters.eq(
                    entity.getIdentifierField().getName(),1), new IndexOptions().unique(true));
        }

        return collection;
    }
}
