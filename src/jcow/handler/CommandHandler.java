package jcow.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jcow.command.ICommand;
import jcow.helpers.CommandHelper;
import jcow.helpers.CommandParseException;
import jcow.helpers.ParameterParseException;

public class CommandHandler {
    
    private final Map<String, ICommand> commands;

    public CommandHandler() {
        this(new HashMap<>());
    }

    public CommandHandler(Map<String, ICommand> commands) {
        this.commands = new HashMap<>(commands);
    }

    /**
     * Whether this handler can handle the command of the given type
     * @param command the command to check
     * @return whether the command can be handled
     */
    public boolean canHandle(String command) {
        if(command == null || command.isEmpty())
            return false;
    
        var commandName = CommandHelper.splitParameters(command)[0];
        return commands.containsKey(commandName);
    }

    /**
     * Registers a new command to the command handler.
     * But it can only register a command if there is no command already registered 
     * with the same command name.
     * @param name the name of the command 
     * @param command the command to register
     * @return whether the command has been registered.
     */
    public boolean register(String name, ICommand command) {
        if(commands.containsKey(name))
            return false;
        commands.put(name, command);
        return true;
    }

    /**
     * Gets a command by its registered name
     * Note this does not handle aliases
     * @param name the name of the command
     * @return the command corresponding with the name
     */
    public ICommand getCommand(String name) {
        if(commands.containsKey(name))
            return commands.get(name);
        throw new NoSuchCommandException("No command with the name '" + name + "' exists for this handler! did you type it correctly and did you use the correct handler?", name);
    }

    /**
     * Invokes a command from the command string.
     * @param command the complete command string 
     * @return the result of the command invokation
     * @throws CommandParseException if the command fails to be parsed, occurs when no command was found or when empty
     * @throws ParameterParseException if parsing of the parameters failed, usually the case when a group has an opening character but not a closing character
     */
    public String invoke(String command) {
        if(command == null || command.isEmpty())
            throw new CommandParseException("Can't parse an empty command!", command, 0);
        
        var splits = CommandHelper.splitParameters(command);
        var commandName = splits[0];
        var cmd = getCommand(commandName);
        if(cmd == null)
            throw new CommandParseException("'" + commandName + "' is not a valid command!", command, 0);
        
        return cmd.invoke(Arrays.copyOfRange(splits, 1, splits.length));
    }
}
