package jcow.handler;

import java.util.Collection;

import jcow.command.ICommand;
import jcow.helpers.CommandParseException;
import jcow.helpers.ParameterParseException;

public interface ICommandHandler {
    
    /**
     * Whether this handler can handle the command of the given type
     * @param command the command to check
     * @return whether the command can be handled
     */
    boolean canHandle(String command);

    /**
     * Registers a new command to the command handler.
     * But it can only register a command if there is no command already registered 
     * with the same command name.
     * @param name the name of the command 
     * @param command the command to register
     * @return whether the command has been registered.
     */
    boolean register(String name, ICommand command);

    /**
     * Gets a command by its registered name
     * Note this does not handle aliases
     * @param name the name of the command
     * @return the command corresponding with the name
     */
    ICommand getCommand(String name);

    /**
     * Invokes a command from the command string.
     * @param command the complete command string 
     * @return the result of the command invokation
     * @throws CommandParseException if the command fails to be parsed, occurs when no command was found or when empty
     * @throws ParameterParseException if parsing of the parameters failed, usually the case when a group has an opening character but not a closing character
     */
    String invoke(String command);
    
    /**
     * Gets A list of the commands which can be handled..
     * @return a list of all of the commands which can be handled
     */
    Collection<String> getCommands();

}
