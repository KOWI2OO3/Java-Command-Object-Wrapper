package jcow.handler.types;

import jcow.command.IContext;
import jcow.utils.ParameterReader;

/**
 * A parser for a certain type
 * 
 * @author KOWI2003
 */
public interface TypeParser<T> {
    
    /**
     * Parses the arguments into the specified type
     * @param context the context of the command
     * @param arguments the arguments to parse
     * @return the parsed type
     */
    T parse(IContext context, ParameterReader arguments);

}
