package jcow.helpers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jcow.command.IContext;
import jcow.handler.types.TypeParser;
import jcow.utils.ParameterReader;

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

    private static final Map<Class<?>, TypeParser<?>> typeParsers = new HashMap<>();

    /**
     * Parses the input string into the given type
     * @param <T> the type to parse to
     * @param type the type to parse to
     * @param annotations the annotations of the parameter (/type)
     * @param context the context of the command
     * @param reader the reader to read the parameters from
     * @return the parsed type
     */
    public static <T> T parseType(Class<T> type, Annotation[] annotations, IContext context, ParameterReader reader) {
        var parser = typeParsers.get(type);
        return parser == null ? null : type.cast(parser.parse(context, reader));
    }

    /**
     * Gets all of the optional parameters from the given parameters input list.
     * 
     * Here optional parameters are parameters that start with a dash (-) having a value after them. 
     * This does mean that the parser must find a value after the optional parameter header.
     * <br></br>
     * Optional parameters without a value should have 2 dashes (--) before them and do not have a value.
     * 
     * @param parameters the list of parameters to get the optionals from
     * @return a map of the optional parameters
     * @throws ParameterParseException when an optional parameter has no value
     */
    public static Map<String, String> getOptionals(String[] parameters) {
        var map = new HashMap<String, String>();
        for(int i = 0; i < parameters.length; i++) {
            var param = parameters[i];
            if(param.startsWith("--")) {
                map.put(param, null);
                continue;
            }else if(param.startsWith("-")) {
                if(i+1 >= parameters.length)
                    throw new ParameterParseException("Failed to parse command parameters as the optional parameter [" + param + "] has no value!", parameters, i);

                var key = param;
                var value = parameters[++i];
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * Removes all of the optional parameters from the given parameters input list
     * @param parameters the list of parameters to remove the optionals from
     * @return a list of the parameters without the optionals
     */
    public static String[] filterOptionals(String[] parameters) {
        var list = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            var param = parameters[i];
            if(param.startsWith("-")) {
                if(!param.startsWith("--")) 
                    i++;
                continue;
            }
            list.add(param);
        }
        return list.toArray(String[]::new);
    }

}
