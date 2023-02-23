package es.brouse.datastorage.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LinkedEntityField {
    /**
     * Get the type of classes that the field
     * will store
     * @return class pointer
     */
    Class<?> pointer();

    /**
     * Get the unique field name to where
     * the content should be stored
     * @return the field unique name
     */
    String name() default "UNDEFINED";
}
