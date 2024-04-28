package jcow.command;

import java.util.Map;

/**
 * A simple command context class containing the simplest context data,
 * this class is used to pass the parameters and flags of a command to the command handler.
 * 
 * @author KOWI2003
 */
public class CommandContext implements IContext {
    
    private String[] parameters;
    private Map<String, String> flags;

    public CommandContext(String[] parameters, Map<String, String> map) {
        this.parameters = parameters;
        this.flags = map;
    }

    @Override
    public String[] getParameters() {
        return parameters;
    }

    @Override
    public Map<String, String> getFlags() {
        return flags;
    }

    @Override
    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }
}
