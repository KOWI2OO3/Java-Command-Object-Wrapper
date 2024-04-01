package jcow.handler;

/**
 * An exception thrown when the handler can't find the actual command
 * 
 * @author KOWI2003
 */
public class NoSuchCommandException extends RuntimeException {

    private final String commandType;

    /** Constructs a new NoSuchCommandException with {@code null} as its
     * detail message.
     * @param commandType the string name that was used to try and get the command
     */
    public NoSuchCommandException(String commandType) {
        super();
        this.commandType = commandType;
    }

    /** Constructs a new NoSuchCommandException with the specified detail message.
     * 
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     * @param commandType the string name that was used to try and get the command
     */
    public NoSuchCommandException(String message, String commandType) {
        super(message);
        this.commandType = commandType;
    }

    /**
     * Gets the string which was used to attempt to get the command with
     * @return the command string name that was not found
     */
    public String getCommandType() {
        return commandType;
    }

}
