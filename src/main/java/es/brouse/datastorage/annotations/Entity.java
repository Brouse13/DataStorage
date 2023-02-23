package es.brouse.datastorage.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    /**
     * Get the unique entity name that will be used
     * create the storage.
     * @return the entity name
     */
    String name() default "UNDEFINED";
}
