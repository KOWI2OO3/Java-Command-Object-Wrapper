package jcow.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When annotated over an class it registers that class as being a command object which means
 * that all its public methods will be mapped to the commands subcommand and all of the public fields
 * are also mapped as subcommands for which their methods will be mapped to the next commands argument
 * 
 * @author KOWI2003
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    
    /**
     * Defines the name of the command. When empty the name will
     * be parsed from the class name
     * @return the name of the command or an empty string if the name should be parsed
     */
    String name() default "";

    /**
     * Defines aliases of the command, this should not include the actual name. 
     * May be empty
     * @return an array of aliases.
     */
    String[] alias() default {}; 

}
