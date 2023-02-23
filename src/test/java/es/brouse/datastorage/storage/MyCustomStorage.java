package es.brouse.datastorage.storage;

import es.brouse.datastorage.exception.StorageException;
import es.brouse.datastorage.storage.operations.Storage;

import java.util.Collection;
import java.util.Optional;

public class MyCustomStorage<T> implements Storage<T> {
    @Override
    public int insert(Collection<T> objects) throws StorageException {
        return 0;
    }

    @Override
    public Optional<Collection<T>> read(Collection<String> identifiers) throws StorageException {
        return Optional.empty();
    }

    @Override
    public Optional<Collection<T>> read(int from, int to) throws StorageException {
        return Optional.empty();
    }

    @Override
    public int update(Collection<T> objects) throws StorageException {
        return 0;
    }

    @Override
    public int delete(Collection<String> identifiers) throws StorageException {
        return 0;
    }
}
