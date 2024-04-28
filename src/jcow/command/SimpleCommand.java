package jcow.command;

import java.util.function.Supplier;

/**
 * Allows for creating a simple command without the needing a special class and interface
 * implementation for it
 * 
 * @author KOWI2003
 */
public class SimpleCommand implements ICommand {

    private final Runnable action;
    private final Supplier<String> actionResult;

    public SimpleCommand(Runnable action) {
        this.actionResult = null;
        this.action = action;
    }

    public SimpleCommand(Supplier<String> action) {
        this.action = null;
        this.actionResult = action;
    }

    @Override
    public String invoke(IContext context) {
        if(action != null)
            action.run();
        if(actionResult != null)
            return actionResult.get();
        return "";
    }
}
