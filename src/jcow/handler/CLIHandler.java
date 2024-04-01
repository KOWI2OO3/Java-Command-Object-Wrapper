package jcow.handler;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * A simple console based interface implementation.
 * @author KOWI2003
 */
public class CLIHandler extends InterfaceHandler {

    // Internal Stream handling
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;
    
    // Settings

    public CLIHandler() {
        this(System.in, System.out , System.err);
    }

    public CLIHandler(InputStream in, PrintStream out) {
        this(in, out, out == System.out ? System.err : out);
    }

    public CLIHandler(InputStream in, PrintStream out, PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;

        if(in == null || out == null)
            throw new IllegalArgumentException("The input and the output streams should not be null!");
    }

    @Override
    protected void handle() {
        try(var stream = new Scanner(in)) {
            while(true) {
                if(stream.hasNext()) {
                    var results = handleCommand(stream.nextLine());
                    for (var result : results)
                        out.println(result);
                }
            }
        }
    }

    @Override
    protected void printError(String error) {
        err.println(error);
    }
}
