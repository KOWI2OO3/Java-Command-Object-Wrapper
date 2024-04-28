package jcow.utils;

import static jcow.helpers.ReflectionHelper.isPrimitive;
import static jcow.helpers.ReflectionHelper.parsePrimitive;

import java.lang.annotation.Annotation;
import java.util.Map;

import jcow.command.IContext;
import jcow.helpers.CommandHelper;

public class ParameterReader {
    
    private final String[] parameters;
    private final Map<String, String> optionals;

    private final IContext context;
    
    private int pointer = 0;

    public ParameterReader(IContext context) {
        this.context = context;
        this.optionals = context.getFlags();
        this.parameters = context.getParameters();
    }

    /**
     * Checks if there are more parameters to read
     * @return true if there are more parameters to read, false otherwise
     */
    public boolean hasNext() {
        return pointer < parameters.length;
    }

    /**
     * Reads the next parameter from the buffer
     * @return The next parameter
     */
    public String readString() {
        return parameters[pointer++];
    }

    /**
     * Reads the next parameter from the buffer and parses it to the specified type if possible.
     * @param <T> the type of the parameter to read
     * @param type the type of the parameter to read
     * @return the parameter read from the buffer
     */
    @SuppressWarnings("unchecked")
    public <T> T read(Class<T> type, Annotation[] annotations) {
        return isPrimitive(type) ? (T) parsePrimitive(readString(), type) : readObject(type, annotations);
    }

    /**
     * Reads the next object from the internal parameter buffer
     * @param <T> the type of the object to read
     * @param type the type of the object to read
     * @return the object read from the buffer
     */
    private <T> T readObject(Class<T> type, Annotation[] annotations) {
        return CommandHelper.parseType(type, annotations, context, this);
    }

    /**
     * Reads an optional parameter from the buffer and parses it to the specified type if possible.
     * If the key does not exist, it will return null.
     * @param <T> the type of the parameter to read
     * @param key the key of the optional parameter
     * @param type the type of the parameter to read
     * @return the parameter read from the buffer
     */
    @SuppressWarnings("unchecked")
    public <T> T readOptional(String key, Class<T> type) {
        if(!optionals.containsKey(key)) return null;
        // TODO: Implement the parsing of optional parameters to the specified type
        return isPrimitive(type) ? (T) parsePrimitive(optionals.get(key), type) : null;
    }

}
