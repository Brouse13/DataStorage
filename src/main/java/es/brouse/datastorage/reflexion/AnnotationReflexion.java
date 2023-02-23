package es.brouse.datastorage.reflexion;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnnotationReflexion {
    private final AnnotatedElement annotatedElement;

    /**
     * AnnotationReflexion constructor to create a new instance.
     * @param annotatedElement element to modify
     */
    public AnnotationReflexion(AnnotatedElement annotatedElement) {
        this.annotatedElement = annotatedElement;
    }

    /**
     * Get if the given element has any annotation present.
     * @return if there's any present annotation
     */
    public boolean hasAnnotations() {
        return Arrays.stream(annotatedElement.getAnnotations()).findAny().isPresent();
    }

    /**
     * Get if the given element has the {@param annotation} present.
     * @param annotation annotation to find
     * @return if the given annotation is present
     * @param <A> annotation class to find
     */
    public <A extends Annotation> boolean isAnnotated(Class<A> annotation) {
        return annotatedElement.isAnnotationPresent(annotation);
    }

    /**
     * Get the classes of each annotation present on the element.
     * If there's no annotation present will return an empty
     * list.
     * @return a list with all the annotations
     */
    public List<Annotation> getAnnotations() {
        return Arrays.stream(annotatedElement.getAnnotations()).collect(Collectors.toList());
    }

    /**
     * Get an {@link Optional<A>} that will contain the found annotation if it's
     * present, otherwise it will return an {@link Optional#empty()}
     * @param annotation annotation to find
     * @return an optional with the found annotation
     * @param <A> annotation class to find
     */
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotation) {
        if (isAnnotated(annotation)) {
            return Optional.of(annotatedElement.getAnnotation(annotation));
        }
        return Optional.empty();
    }
}
