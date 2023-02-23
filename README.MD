# DataStorage

This library is created with the intention of hiding and abstracting the database 
serialization to the rest of programmers to make them easier their job on the moment 
to use a database.

At the moment the library supports `Mysql` and `MongoDB` but is intended to add more 
possibilities in the future.

## Entity
To work with the library the first step is to create an entity to define its fields, 
so then it can be parsed and saved properly. To do that you have to use annotations 
of package `es.brouse.datastorage.annotations`.

- `@Entity` Define how the entity will be represented on the database with fields 
like:
  - **Name** Default class name.


- `@EntityField` Define how each field will be represented on the database with fields
  like:
  - **Name** Default field name.
  - **Size** Size on the database.
  - **NotNull** Indicates if the field can be null
  - **Unique** Indicates if the field must be unique
  

- `@EntityIdentifier` Defines which will be the identifier of the table, this will mean 
that the field associated to it will have the following properties: 
  - Unique: true
  - NotNull: true
 
**NOTE:** The entity must have only one field annotated with `@EntityIdentifier` and this field 
will also have to contain the `@EntityField` annotation.

This is an example of a valid Entity:

````java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "TestEntity")
@AllArgsConstructor
public class TestEntity {

    @EntityIdentifier
    @EntityField(name = "identifier", size = 50, unique = true)
    @Getter
    public String identifier;

    @EntityField
    @Getter @Setter public int value;
}
````


## Storage
The library has the possibility to be customized by the programmer adding
new Storage methods. This is done creating implementing the `Storage<T>` interface.
And the registering it on the library.

```
//Get the DataStorage singleton instance
DataStorage dataStorage = DataStorage.getInstance();

//Get the StorageManager to register the new Storage<T>
StorageSelector storageSelector = dataStorage.getStorageSelector();
storageSelector.register("custom_name", MyCustomStorage.class);
```

Otherwise, if you want to use the provided storage methods you will have to get a DataStorage 
instance and get the correct instance providing the correct serializable entity as an argument.

```
//Get the DataStorage singleton instance
DataStorage dataStorage = DataStorage.getInstance();

//Get the StorageManager to register the new Storage<T>
StorageSelector storageSelector = dataStorage.getStorageSelector();

//Specify mysql (Default) / mongodb
storageSelector.setActive("name");

//Use the serializer to store
Storage<TestEntity> activeStorage = storageSelector.getActiveStorage(TestEntity.class);
```

## CRUD Operations
When you have the correct instance of the storage that you want to use. The only step left is 
to specify the operation to perform.
**NOTE:** As arguments it needs a `Collection<?>` the best option is to use Sets to avoid duplicate 
keys.

```
//Insert
activeStorage.insert(Sets.newHashSet(new TestEntity("test", 10), new TestEntity("test1", 100)));

//Read
activeStorage.read(Sets.newHashSet("identifier1"));
activeStorage.read(1, 2);

//Update (You mustn't modify id of the entity)
activeStorage.update(Sets.newHashSet(new TestEntity("test", 100)));

//Delete
activeStorage.delete(Sets.newHashSet("identifier1", "identifier2"));
```