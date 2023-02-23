package es.brouse.datastorage.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

public class WrappedEntity<T> {
    @Getter private final String name;

    @Getter private final Class<T> clazz;

    @Getter private final WrappedField identifierField;
    private final Map<String, WrappedField> fields;

    private WrappedEntity(String name, Class<T> clazz, WrappedField identifierField, Map<String, WrappedField> fields) {
        this.name = name;
        this.clazz = clazz;
        this.identifierField = identifierField;
        this.fields = fields;
    }

    public Map<String, WrappedField> getFields() {
        return ImmutableMap.copyOf(fields);
    }

    public static <T> Builder<T> getBuilder(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    @Override
    public String toString() {
        return "ParsedEntity{" +
                "name='" + name + '\'' +
                ", clazz=" + clazz +
                ", identifierField=" + identifierField +
                ", fields=" + fields +
                '}';
    }

    public static class Builder<T> {
        private String name;

        private WrappedField identifierField;

        private Map<String, WrappedField> fields;
        private final Class<T> clazz;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> name(String name) {
            this.name = name.replaceAll(" ", "_");
            return this;
        }

        public Builder<T> fields(Map<String, WrappedField> fields) {
            this.fields = fields;
            return this;
        }

        public Builder<T> identifierField(WrappedField identifierField) {
            this.identifierField = identifierField;
            return this;
        }

        public WrappedEntity<T> build() {
            Preconditions.checkNotNull(name, "Entity must have a notNull name");
            Preconditions.checkNotNull(clazz, "Entity must have a linked class");
            Preconditions.checkNotNull(identifierField, "Entity must have a valid EntityIdentifier field");
            Preconditions.checkNotNull(fields, "Entity fields cannot be empty");
            return new WrappedEntity<>(name, clazz, identifierField, fields);
        }
    }
}
