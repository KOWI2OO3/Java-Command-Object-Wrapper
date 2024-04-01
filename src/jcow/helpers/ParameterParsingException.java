package jcow.helpers;

public class ParameterParsingException extends RuntimeException {
    
    private int indexAtFault = -1;
    private String[] originalInput = null;

    /** Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public ParameterParsingException(String[] input, int indexAtFault) {
        super();
        this.indexAtFault = indexAtFault;
        this.originalInput = input;
    }

    /** Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public ParameterParsingException(String message, String[] input, int indexAtFault) {
        super(message);
        this.indexAtFault = indexAtFault;
        this.originalInput = input;
    }

    /**
     * The expected index where the mistake has been found of the array of the parameters splits.
     * call {@link ParameterParsingException#getInputSplits getInputSplits} to get this array of parameters
     * @return the index of the mistake of the array of parameters
     */
    public int getSplitIndexAtFault() {
        return indexAtFault;
    }

    /**
     * The expected index where the mistake has been found.
     * call {@link ParameterParsingException#getInput getInput} to get this array of parameters
     * @return the index of the mistake in the given input string
     */
    public int getIndexAtFault() {
        int sum = 1;
        for (int i = 0; i < indexAtFault; i++) 
            sum += originalInput[i].length() + 1;
        
        return sum;
    }

    /**
     * The array of input strings used to parse the parameters.
     * 
     * @see ParameterParsingException#getSplitIndexAtFault() getSplitIndexAtFault
     * @return the parameters array
     */
    public String[] getInputSplits() {
        return originalInput;
    }

    /**
     * Gets the input as one whole string.
     * @see ParameterParsingException#getIndexAtFault() getIndexAtFault
     * @return the input given to the original function
     */
    public String getInput() {
        var result = "";
        for (var input : originalInput) {
            if(!result.isEmpty())
                result += " " + input;
            else
                result += input;
        }
        return result;
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
