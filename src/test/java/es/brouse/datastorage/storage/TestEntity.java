package es.brouse.datastorage.storage;

import es.brouse.datastorage.annotations.*;


@Entity(name = "TestEntity")
public class TestEntity {
    public TestEntity(String name, int value) {
        this.identifier = name;
        this.value = value;
    }

    @EntityIdentifier
    @EntityField(name = "identifier", size = 50, unique = true)
    public String identifier;

    @EntityField
    public int value;

    @Override
    public String toString() {
        return "TestEntity{" + "identifier='" + identifier + '\'' + ", value=" + value + '}';
    }
}
