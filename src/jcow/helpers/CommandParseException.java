package jcow.helpers;


/**
 * An command parse exception which is thrown when an command couldn't be parsed properly.
 * it contains information about the exception that occured when parsing the command.
 * 
 * @author KOWI2003
 */
public class CommandParseException extends RuntimeException {
    
    private int indexAtFault = -1;
    private String input = null;

    /** Constructs a new CommandParseException with {@code null} as its
     * detail message.
     * @param input the given string to parse as command
     * @param indexAtFault the expected cause of the failure
     */
    public CommandParseException(String input, int indexAtFault) {
        super();
        this.indexAtFault = indexAtFault;
        this.input = input;
    }

    /** Constructs a new CommandParseException with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     * @param input the given string to parse as command
     * @param indexAtFault the expected cause of the failure
     */
    public CommandParseException(String message, String input, int indexAtFault) {
        super(message);
        this.indexAtFault = indexAtFault;
        this.input = input;
    }

    /**
     * The expected index where the mistake has been found.
     * call {@link ParameterParseException#getInput getInput} to get this array of parameters
     * @return the index of the mistake in the given input string
     */
    public int getIndexAtFault() {
        return indexAtFault;
    }

    /**
     * Gets the input as one whole string.
     * @see ParameterParseException#getIndexAtFault() getIndexAtFault
     * @return the input given to the original function
     */
    public String getInput() {
        return input;
    }

    @Override
    public String getMessage() {
        return getErrorMessage() + System.lineSeparator() + getFaultDisplayMessage();
    }

    /**
     * Gets only the error message displayed to tell what's wrong, this means that the
     * fault display which shows which index is incorrect is not included 
     * @return the error message hinting at the cause
     */
    public String getErrorMessage() {
        return super.getMessage().replace("%index%", getIndexAtFault() + "");
    }

    /**
     * Gets the fault display showing which character is the suspected cause
     * <br></br>
     * <b>Note</b>: this only works when the characters printed have the same length, therefor it
     * probably doesn't work on a custom font
     * @return the fault display message showing the cause
     */
    public String getFaultDisplayMessage() {
        return getInput() + System.lineSeparator() +
            " ".repeat(getIndexAtFault()-1) + "^";
    }

}
