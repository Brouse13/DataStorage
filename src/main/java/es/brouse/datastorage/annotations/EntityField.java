package es.brouse.datastorage.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityField {
    /**
     * Get the unique field name to where
     * the content should be stored.
     *
     * @return the field unique name
     */
    String name() default "UNDEFINED";

    /**
     * Get if the field content should be unique
     * on the database, or it can be repeated.
     *
     * @return if content must be unique
     */
    boolean unique() default false;

    /**
     * Get if the field can be null on the database.
     * By default, all {@link EntityIdentifier} will have
     * this property.
     *
     * @return if the content can be null
     */
    boolean notNull() default false;

    /**
     * Get the default size that the item can have on
     * the database. It's normally used to fixed length on
     * {@link String}.
     *
     * @return max size on the database
     */

    int size() default 25;
}
