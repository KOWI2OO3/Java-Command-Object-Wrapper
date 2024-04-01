package jcow.helpers;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple helper class aiding in the parsing and handling of commands
 * 
 * @author KOWI2003
 */
public final class CommandHelper {
    
    private CommandHelper() {}

    /**
     * Splits the parameters based on the few simple rules of: 
     * <ul>
     * <li>Spaces split parameters</li>
     * <li>' brackets makes a section one parameter</li>
     * <li>" brackets makes a section one parameter, and encompasses ' sections</li>
     * </ul>
     * <br></br>
     * this method also cleans any formatting characters like the brackets used
     * @param input the input string to split for parameters
     * @return an array of the input splits
     * @throws ParameterParseException when a group can not be properly constructed, usually the case when a group has an opening character but not a closing character
     */
    public static String[] splitParameters(String input) {
        if(!input.contains("\"") && !input.contains("'"))
            return input.split(" ");

        var splits = input.split(" ");
        return Arrays.stream(
            group(group(splits, "\""), "'"))
            .map(CommandHelper::cleanOuterBrackets)
            .toArray(String[]::new);
    }
    
    /**
     * Cleans out the brackets surrounding the string
     * @param input the string which needs to be cleaned
     * @return the cleaned string or the original string if the string was already clean
     */
    private static String cleanOuterBrackets(String input) {
        if(input.startsWith("\"") || input.startsWith("'"))
            return input.substring(1, input.length()-1);
        return input;
    }

    /**
     * Groups elements of the array together based on the groupBy argument
     * for example: grouping ["'Hello", "World'", "and", "Java"] by ' becomes ["'Hello World'", "and", "Java"]
     * Want to group by an opening and closing condition use {@link CommandHelper#group(String[], String, String) group(input, start, end)}
     * <br></br>
     * <b>Note</b>: this method also cleans the groupping characters, 
     * use {@link CommandHelper#group(String[], String) group} to group without cleaning
     * @param input the input array for which elements should be grouped [should not be null]
     * @param groupBy the identifier to group by
     * @return the new grouped array
     * @throws ParameterParseException when a group can not be properly constructed, usually the case when a group has an opening character but not a closing character
     */
    public static String[] groupAndClean(String[] input, String groupBy) {
        return groupAndClean(input, groupBy, groupBy);
    }

    /**
     * Groups elements of the array together based on the groupBy argument
     * for example: grouping ["'Hello", "World'", "and", "Java"] by ' becomes ["'Hello World'", "and", "Java"]
     * Want to group by an opening and closing condition use {@link CommandHelper#group(String[], String, String) group(input, start, end)}
     * <br></br>
     * <b>Note</b>: this method does not clean the groupping characters,
     * use {@link CommandHelper#groupAndClean(String[], String) groupAndClean} to also clean the character
     * @param input the input array for which elements should be grouped [should not be null]
     * @param groupBy the identifier to group by
     * @return the new grouped array
     * @throws ParameterParseException when a group can not be properly constructed, usually the case when a group has an opening character but not a closing character
     */
    public static String[] group(String[] input, String groupBy) {
        return group(input, groupBy, groupBy);
    }

    /**
     * Groups elements of the array together based on the groupBy arguments
     * for example: grouping ["(Hello", "World)", "and", "Java"] by ( and ) becomes ["(Hello World)", "and", "Java"]
     * Want to group by an opening and closing condition use {@link CommandHelper#group(String[], String, String) group(input, start, end)}
     * <br></br>
     * <b>Note</b>: this method also cleans the groupping characters, 
     * use {@link CommandHelper#group(String[], String, String) group} to group without cleaning
     * @param input the input array for which elements should be grouped [should not be null]
     * @param groupByStart the identifier to start a group with
     * @param groupByEnd the identified to end a group with 
     * @return the new grouped array
     * @throws ParameterParseException when a group can not be properly constructed, usually the case when a group has an opening character but not a closing character
     */
    public static String[] groupAndClean(String[] input, String groupByStart, String groupByEnd) {
        var result = group(input, groupByStart, groupByEnd);
        for (int i = 0; i < input.length; i++) {
            if(input[i].startsWith(groupByStart) && input[i].endsWith(groupByEnd))
                input[i] = input[i].substring(1, input[i].length()-1);
        }
        return result;
    }

    /**
     * Groups elements of the array together based on the groupBy arguments
     * for example: grouping ["(Hello", "World)", "and", "Java"] by ( and ) becomes ["(Hello World)", "and", "Java"]
     * Want to group by an opening and closing condition use {@link CommandHelper#group(String[], String, String) group(input, start, end)}
     * <br></br>
     * <b>Note</b>: this method does not clean the groupping characters, 
     * use {@link CommandHelper#groupAndClean(String[], String, String) groupAndClean} to also clean the character
     * @param input the input array for which elements should be grouped [should not be null]
     * @param groupByStart the identifier to start a group with
     * @param groupByEnd the identified to end a group with 
     * @return the new grouped array
     * @throws ParameterParseException when a group can not be properly constructed, usually the case when a group has an opening character but not a closing character
     */
    public static String[] group(String[] input, String groupByStart, String groupByEnd) {
        if(input == null || groupByStart == null)
            return input;

        var result = new ArrayList<String>();
        for (int i = 0; i < input.length; i++) {
            var param = input[i];
            if(!param.startsWith(groupByStart) || (param.startsWith(groupByStart) && param.endsWith(groupByEnd))) {
                result.add(param);
                continue;
            }
            
            var combined = "";
            var tmp = Arrays.copyOfRange(input, i, input.length);
            for (int j = 0; j < tmp.length; j++) {
                var next = tmp[j];
                
                if(!combined.isEmpty())
                    next = " " + next;
                combined += next;

                if(next.endsWith(groupByEnd)) {
                    i+=j;
                    break;
                }

                if(j == tmp.length-1)
                    throw new ParameterParseException("Failed to parse command parametes as the bracket [" + groupByStart + "] at index %index% is never closed!", input, i);
            }

            result.add(combined);
        }
        return result.toArray(String[]::new);
    }

}
