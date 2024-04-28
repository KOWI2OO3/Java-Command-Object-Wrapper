import jcow.command.SimpleCommand;
import jcow.command.annotations.Controller;
import jcow.handler.CLIHandler;
import jcow.handler.CommandWrapper;

public class App {
    public static void main(String[] args) throws Exception {
        var cli = new CLIHandler(System.in, System.out);
        cli.attachHelpCommand();
        
        var commandHandler = cli.constructHandler();

        commandHandler.register("test", new CommandWrapper(TestCommand.class));

        // Registers a new command called by 'hello'
        commandHandler.register("hello", new SimpleCommand(() -> System.out.println("say hi")));

        cli.start();

        // // This will print 'say hi'
        // cli.execute("hello");
    }

    public static class TestCommand {

        @Controller(isDefault = true)
        public static String run(String msg) {
            return msg;
        }

        @Controller("Something")
        public static String run(String msg, int i) {
            return msg.repeat(i);
        }
        
    }
}
