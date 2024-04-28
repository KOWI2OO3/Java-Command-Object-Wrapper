package jcow.command;

import java.util.Collection;
import java.util.List;

public interface ICommand {
    
    /**
     * Invokes the command with the given parameters
     * @param params the parameters to invoke the command with
     * @return the result of the command
     */
    String invoke(IContext context);

    /**
     * Returns a list of possible completions for the given context
     * @param context the context to complete
     * @return a list of possible completions
     */
    default Collection<String> nextCompletion(String context) {
        return List.of();
    }

    /**
     * Gets a usage string for the command as displayed on the help command
     * @return the usage string
     */
    default String getUsage() { return ""; } 

}
