package jcow.helpers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CommandHelperTest {

    @Test
    void testSimpleSplitInput() {
        var input = "Test parameter parsing simple";
        var expected = new String[] {"Test","parameter", "parsing", "simple"};
        assertArrayEquals(expected, CommandHelper.splitParameters(input));
    }

    @Test
    void testStringParameterSplitInput() {
        var input = "Test parameter \"parsing simple\"";
        var expected = new String[] {"Test","parameter", "parsing simple"};
        assertArrayEquals(expected, CommandHelper.splitParameters(input));
    }

    @Test
    void testCharBracketGroupingSplitInput() {
        var input = "Test parameter 'parsing simple'";
        var expected = new String[] {"Test","parameter", "parsing simple"};
        assertArrayEquals(expected, CommandHelper.splitParameters(input));
    }

    @Test
    void testStringAndCharBracketGroupingSplitInput() {
        var input = "Test \"parameter 'parsing simple'\"";
        var expected = new String[] {"Test","parameter 'parsing simple'"};
        assertArrayEquals(expected, CommandHelper.splitParameters(input));
    }

    @Test
    void testGroupSimple() {
        var test = "some test of splitting";
        var expected = test.split(" ");
        assertArrayEquals(expected, CommandHelper.group(expected, "\""));
    }

    @Test
    void testGroup() {
        var test = "some test \"of splitting\"";
        var expected = new String[] {"some", "test", "\"of splitting\""};
        assertArrayEquals(expected, CommandHelper.group(test.split(" "), "\""));
    }

    @Test
    void testGroupIncorrectFormat() {
        var test = "some test \"of splitting\" \"Hi";
        var splits = test.split(" ");
        assertThrows(ParameterParseException.class, () -> CommandHelper.group(splits, "\""));
    }
}
