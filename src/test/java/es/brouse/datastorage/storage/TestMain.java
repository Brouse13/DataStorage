package es.brouse.datastorage.storage;

import com.google.common.collect.Sets;
import es.brouse.datastorage.DataStorage;
import es.brouse.datastorage.StorageSelector;
import es.brouse.datastorage.entity.EntityParser;
import es.brouse.datastorage.exception.ReflexionException;
import es.brouse.datastorage.exception.StorageException;
import es.brouse.datastorage.storage.operations.Storage;

public class TestMain {
    public static void main(String[] args) throws StorageException, ReflexionException {
        //Get the DataStorage singleton instance
        DataStorage dataStorage = DataStorage.getInstance();

        //Get the StorageManager to register the new Storage<T>
        StorageSelector storageSelector = dataStorage.getStorageSelector();
        storageSelector.register("custom_name", MyCustomStorage.class);


        //Register valid entities
        dataStorage.addEntity(EntityParser.parse(TestEntity.class));


        //Get the StorageManager to register the new Storage<T>
        storageSelector.setActive("<mysql (Default) / mongodb>");

        //Use the serializer to store
        Storage<TestEntity> activeStorage = storageSelector.getActiveStorage(TestEntity.class);
        //Insert
        activeStorage.insert(Sets.newHashSet(new TestEntity("test", 10), new TestEntity("test1", 100)));
        //Read
        activeStorage.read(Sets.newHashSet("identifier1"));
        activeStorage.read(1, 2);
        //Update (You mustn't modify id of the entity)
        activeStorage.update(Sets.newHashSet(new TestEntity("test", 100)));
        //Delete
        activeStorage.delete(Sets.newHashSet("identifier1", "identifier2"));
    }
}
