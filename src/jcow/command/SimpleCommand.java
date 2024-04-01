package jcow.command;

import java.util.function.Supplier;

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
    public String invoke(String... params) {
        if(action != null)
            action.run();
        if(actionResult != null)
            return actionResult.get();
        return "";
    }
}
