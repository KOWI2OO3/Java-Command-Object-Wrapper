package jcow.command;

import java.util.Map;

/**
 * Represents the context of a command that is being executed.
 * An interface to allow for external extensions to the command system.
 * 
 * @author KOWI2003
 */
public interface IContext {
 
    /**
     * Gets the parameters of the command in the order they were given
     * @return an array of the parameters of the command
     */
    String[] getParameters();

    /**
     * Gets the flags of the command in a map with the flag name as the key and the flag value as the value
     * these are filtered from the given parameters as they start with a dash or double dash.
     * 
     * If a flag is not set it will not appear in the map, and if its a flag without a value the value will be null
     * @return a map of the flags of the command
     */
    Map<String, String> getFlags();

    /**
     * Sets the parameters of the command
     * @param parameters an array of the parameters of the command
     */
    void setParameters(String[] parameters);
    
}
