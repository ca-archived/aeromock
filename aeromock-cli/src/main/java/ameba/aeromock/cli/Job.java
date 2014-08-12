package ameba.aeromock.cli;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark Job class.
 * @author stormcat24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Job {

    /**
     * Return job name. {@code name} is unique id to identify job.
     * @return job name
     */
    String name();

    /**
     * @return description of job
     */
    String description();

}
