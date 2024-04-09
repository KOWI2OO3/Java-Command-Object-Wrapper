import jcow.command.SimpleCommand;
import jcow.handler.CLIHandler;
import jcow.handler.HelpCommandHandler;

public class App {
    public static void main(String[] args) throws Exception {
        var cli = new CLIHandler(System.in, System.out);
        var commandHandler = cli.constructHandler();
        cli.attachHandler(new HelpCommandHandler(cli));

        // Registers a new command called by 'hello'
        commandHandler.register("hello", new SimpleCommand(() -> System.out.println("say hi")));

        cli.start();
    }
}
