package jcow.handler;

import java.util.Collection;
import java.util.List;

import jcow.command.ICommand;

public class HelpCommandHandler implements ICommandHandler {

    private InterfaceHandler ui;

    public HelpCommandHandler(InterfaceHandler ui) {
        this.ui = ui;
    }

    @Override
    public boolean canHandle(String command) {
        return command.startsWith("help");
    }

    @Override
    public boolean register(String name, ICommand command) {
        throw new UnsupportedOperationException("Cannot add commands to an 'help' command handler!");
    }

    @Override
    public ICommand getCommand(String name) {
        return null;
    }

    @Override
    public String invoke(String command) {
        var commandList = "'\n All Available Commands: \n";
        for(var handler : ui.handlers) {
            if(handler == this)
                continue;
            for(var cmd : handler.getCommands()) 
                commandList += "- " + cmd + "\n";
        }
        return commandList;
    }

    @Override
    public Collection<String> getCommands() {
        return List.of();
    }

}
