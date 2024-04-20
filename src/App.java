import jcow.command.SimpleCommand;
import jcow.handler.CLIHandler;

public class App {
    public static void main(String[] args) throws Exception {
        var cli = new CLIHandler(System.in, System.out);
        cli.attachHelpCommand();
        
        var commandHandler = cli.constructHandler();

        // Registers a new command called by 'hello'
        commandHandler.register("hello", new SimpleCommand(() -> System.out.println("say hi")));

        cli.start();
    }
}
