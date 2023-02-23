package es.brouse.datastorage.reflexion;

import es.brouse.datastorage.exception.ReflexionException;
import jdk.nashorn.internal.runtime.options.Option;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassReflexion<T> {
    private final Class<T> clazz;

    /**
     * ClassReflexion constructor to create a new instance.
     *
     * @param clazz class to bind
     */
    public ClassReflexion(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Get the name of the class
     * @return class name
     */
    public String getName() {
        return clazz.getSimpleName();
    }

    /**
     * Get the field from the class with the given name.
     * It will ignore access modifiers.
     *
     * @param name field name
     * @return the field tht matches with the name
     * @throws ReflexionException if any reflexion problem happens
     */
    public Field getField(String name) throws ReflexionException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new ReflexionException("Field not found");
        }
    }

    /**
     * Get all the fields that are present on the class
     * stored in a {@link Set}.
     *
     * @return all the class fields
     */
    public Set<Field> getFields() {
        return Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toSet());
    }

    /**
     * Cast the given object to the class object that represents this class.
     * If there's no cast match, it will return an empty optional.
     *
     * @param object object to cast
     * @return an optional with the cast object
     */
    public Optional<T> cast(Object object) {
        if (clazz.isInstance(object)) {
            return Optional.of(clazz.cast(object));
        }
        return Optional.empty();
    }

    /**
     * Create a new instance of the given class with {@param args}.
     *
     * @param args args that need the class to been instanced
     * @return the created instance
     * @throws ReflexionException if any reflexion problem happens
     */
    public T instance(Object... args) throws ReflexionException {
        //Instance the constructor
        try {
            return clazz.getConstructor(toClassArray(args)).newInstance(args);
        } catch (NoSuchMethodException e) {
            throw new ReflexionException("Constructor not found for given args");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new ReflexionException("Unable to instance class " + clazz.getSimpleName());
        }
    }

    /**
     * Invoke the given method on the {@param clazz} with the given {@param args}.
     *
     * @param entity instanced class where the method is
     * @param name method name
     * @param args args to invoke the method
     * @return the method return content
     * @throws ReflexionException if any reflexion problem happens
     */
    public Object invokeMethod(Object entity, String name, Object... args) throws ReflexionException {
        try {
            Method method = clazz.getDeclaredMethod(name, toClassArray(args));
            return method.invoke(entity, args);
        } catch (NoSuchMethodException e) {
            throw new ReflexionException("Unable to get method with given args");
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ReflexionException("Unable to invoke method " + name);
        }
    }

    /**
     * Transform the given array into its class array.
     * It's used to help class reflexion
     * @param objects objects to get its classes
     * @return the class array
     */
    private Class<?>[] toClassArray(Object[] objects) {
        return Arrays.stream(objects).map(Object::getClass).toArray(Class<?>[]::new);
    }
}
