package jcow.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: Rename!!
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    
    boolean isDefault() default false;

    /**
    * Defines the name of the controller. When empty the name will
    * be parsed from the member name
    * @return the name of the controller or an empty string if the name should be parsed
    */
    String value() default "";
}
