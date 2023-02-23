package es.brouse.datastorage.adapters;

public interface TypeAdapter<T> {
    /**
     * Decode the given reader into a new {@param T} type
     * @param reader reader to decode
     * @return the created entity
     */
    T decode(EntityReader reader);

    /**
     * Encode the given {@param object} into a new {@link EntityWriter}
     * @param object object to encode
     * @return the encoded item
     */
    EntityWriter encode(T object);
}
