package es.brouse.datastorage.storage.operations;

import com.google.common.collect.Sets;
import es.brouse.datastorage.DataStorage;
import es.brouse.datastorage.entity.WrappedEntity;
import es.brouse.datastorage.entity.WrappedField;
import es.brouse.datastorage.exception.ReflexionException;
import es.brouse.datastorage.exception.StorageException;
import es.brouse.datastorage.reflexion.Clazz;
import es.brouse.datastorage.storage.MySQLHelper;
import es.brouse.datastorage.storage.objects.Mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MySQLStorage<T> implements Storage<T> {
    private final MySQLHelper operations = new MySQLHelper();
    private final WrappedEntity<T> entity;
    private final Class<T> clazz;

    public MySQLStorage(Class<T> clazz) throws StorageException {
        this.clazz = clazz;
        this.entity = DataStorage.getInstance().getEntity(clazz)
                .orElseThrow(() -> new StorageException("Unparsed entity: " + clazz.getSimpleName()));;

        //On construct class create the table if not exists
        createTable();
    }

    private void createTable() {
        //We map the identifiers to it's sql in a set to avoid double operations
        try {
            executeUpdate(operations.generateTable(entity), true);
        } catch (StorageException e) {
            //Ignore database creation errors, can be alter table error
        }
    }

    @Override
    public int insert(Collection<T> objects) throws StorageException {
        if (objects.isEmpty()) return 0;

        //We map the identifiers to it's sql in a set to avoid double operations
        return executeUpdate(objects.stream()
                .map(object -> operations.generateInsert(entity, object))
                .collect(Collectors.toSet()), false);
    }

    @Override
    public Optional<Collection<T>> read(Collection<String> identifiers) throws StorageException {
        if (identifiers.isEmpty()) return Optional.empty();

        //We map the identifiers to it's sql in a set to avoid double operations
        return Optional.of(executeQuery(identifiers.stream()
                .map(identifier -> operations.generateSelect(entity, identifier))
                .collect(Collectors.toSet())));
    }

    @Override
    public Optional<Collection<T>> read(int from, int to) throws StorageException {
        if (from > to || from <= 0) return Optional.of(Sets.newHashSet());

        //We map the identifiers to it's sql in a set to avoid double operations
        return Optional.of(executeQuery(Sets.newHashSet(
                operations.generateSelect(entity, from - 1, to))));
    }

    @Override
    public int update(Collection<T> objects) throws StorageException {
        if (objects.isEmpty()) return 0;

        //We map the identifiers to it's sql in a set to avoid double operations
        return executeUpdate(objects.stream()
                .map(object -> operations.generateUpdate(entity, object))
                .collect(Collectors.toSet()), false);
    }

    @Override
    public int delete(Collection<String> identifiers) throws StorageException {
        if (identifiers.isEmpty()) return 0;

        //We map the identifiers to it's sql in a set to avoid double operations
        return executeUpdate(Sets.newLinkedHashSet(identifiers.stream()
                .map(identifier -> operations.generateDelete(entity, identifier))
                .collect(Collectors.toSet())), false);
    }

    /**
     * Main (update / delete / insert) code to perform Update operations into the database.
     *
     * @param operations Set of all the sql to execute
     * @return the amount of operation performed successfully
     * @throws StorageException if any exception happens during storage
     */
    private int executeUpdate(Collection<String> operations, boolean forceStop) throws StorageException {
        int count = 0;

        //Open a new connection and perform all the given sql
        try(Connection connection = Mysql.getConnection()) {
            for (String sql : operations) {

                //Increase the counter if the operation result successfully
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    if (!statement.execute()) {
                        count++;
                    }else {
                        if (forceStop) return 0;
                    }
                }
            }
        }catch (SQLException exception) {
            throw new StorageException(exception);
        }

        return count;
    }

    /**
     * Main (select) code to perform Query operations into the database.
     *
     * @param operations Set of all the sql to execute
     * @return the amount of operation performed successfully
     * @throws StorageException if any exception happens during storage
     */
    private Collection<T> executeQuery(Set<String> operations) throws StorageException {
        Collection<T> queryResult = Sets.newHashSet();

        //Open a new connection and perform all the given sql
        try(Connection connection = Mysql.getConnection()) {
            for (String sql : operations) {

                //Prepare the statement to perform a query sentence
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    try(ResultSet resultSet = statement.executeQuery()) {
                        //Avoid null ResultSets
                        while (resultSet.next()) {
                            final LinkedHashSet<Object> readObjects = Sets.newLinkedHashSet();
                            //For each field present on the entity try to get it from the database
                            try {
                                for (WrappedField value : entity.getFields().values()) {
                                    readObjects.add(resultSet.getObject(value.getName(), value.getClazz()));
                                }

                                queryResult.add(Clazz.getClassManager(clazz).instance(readObjects.toArray()));
                            } catch (SQLException | ReflexionException e) {
                                throw new StorageException(e);
                            }
                        }
                    }
                }
            }
        }catch (SQLException exception) {
            throw new StorageException(exception);
        }

        return queryResult;
    }
}
