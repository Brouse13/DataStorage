package es.brouse.datastorage.storage.operations;

import es.brouse.datastorage.exception.StorageException;

import java.util.Collection;
import java.util.Optional;

public interface Storage<T> {
    /**
     * Insert a new object into the database instance. If the collection
     * is empty it won't perform any changes and return the amount of items
     * that have been affected.
     *
     * @apiNote It won't overwrite existent data
     * @param objects objects to create
     * @return the amount of items added to the database
     * @throws StorageException if any exception happens during storage
     */
    int insert(Collection<T> objects) throws StorageException;

    /**
     * Read from the database all the items that have a matching identifier
     * with the provided on the {@param identifiers}. If there are no matches,
     * the optional will be a {@link Optional#empty()}.
     *
     * @param identifiers identifiers to get from the database
     * @return all the found items if present
     * @throws StorageException if any exception happens during storage
     */
    Optional<Collection<T>> read(Collection<String> identifiers) throws StorageException;

    /**
     * Read from the database all the items that are stored on the range of
     * {@param from} and {@param to}.If there are no matches,  the optional
     * will be a {@link Optional#empty()}.
     * @apiNote 'to' must be higher than 'from' and 'from' must be grater to 0
     *
     * @param from first index to check on the database
     * @param to last index to check on the database
     * @return all the found identifiers if present
     * @throws StorageException if any exception happens during storage
     */
    Optional<Collection<T>> read(int from, int to) throws StorageException;

    /**
     * Update all the items that are stored on the collection. If the items
     * are not present on the database, it won't create a new one.
     *
     * @param objects to update
     * @return the amount of items that have been updated
     * @throws StorageException if any exception happens during storage
     */
    int update(Collection<T> objects) throws StorageException;

    /**
     * Delete all the items that matches with the given identifiers.
     *
     * @param identifiers identifiers to delete
     * @return the amount of items deleted
     * @throws StorageException if any exception happens during storage
     */
    int delete(Collection<String> identifiers) throws StorageException;
}
