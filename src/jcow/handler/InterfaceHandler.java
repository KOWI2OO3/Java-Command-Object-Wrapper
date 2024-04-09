package jcow.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class InterfaceHandler {
    
    private final Thread thread;
    final Set<ICommandHandler> handlers;

    // Settings
    // Whether to allow multiple handlers to handle the same command
    protected boolean multiHandleCommands = false;
    protected boolean printStacktrace = false;

    public InterfaceHandler() {
        this.handlers = new HashSet<>();
        this.thread = new Thread(this::handle);
    }

    /**
     * Starts the handling of the interface, 
     * but only if it isn't currently running
     */
    public void start() {
        if(!thread.isAlive())
        thread.start();
    }
    
    /**
     * Stops the handling of the interface
     */
    public void stop() {
        thread.interrupt();
    }
    
    /**
     * Creates a new command handler which is already attached to the interface 
     * @return the attached command handler
     */
    public ICommandHandler constructHandler() {
        var handler = new CommandHandler();
        attachHandler(handler);
        return handler;
    }

    /**
     * Attaches a command handler such that it can be used to handle incomming commands
     * @param handler the handler to attach [not null]
     * @return whether the handler has been attached
     */
    public boolean attachHandler(ICommandHandler handler) {
        return handler != null && handlers.add(handler);
    }

    /**
     * Dettaches a command handler from the interface
     * @param handler the handler to detach 
     * @return whether the handler has been dettached, note: it will return false if the handler wasn't attached
     */
    public boolean dettachHandler(ICommandHandler handler) {
        if(handler == null)
            return false;
        return handlers.remove(handler);
    }
    
    /**
     * Checks whether the attached handlers can handle a given command
     * @param command the command to check
     * @return whether the command can be handled
     */
    public boolean canHandle(String command) {
        for (var handler : handlers) {
            if(handler.canHandle(command))
                return true;
        }
        return false;
    }

    /**
     * Called async on start and should handle the continious checking and 
     * forwarding the command to {@link InterfaceHandler#handleCommand(String) handleCommand} 
     */
    protected abstract void handle();

    protected abstract void printError(String error);

    /**
     * Handles the command given if it can, and returns the results of the handler(s)
     * @param command the command to handle
     * @return the result as gotte from the handler(s)
     */
    protected String[] handleCommand(String command) {
        if(handlers == null)
            return new String[0];
        if(!canHandle(command)) {
            printError("Unknown command.");
            return new String[0];
        }
        
        var results = new ArrayList<String>();
        for (var handler : handlers) {
            if(handler.canHandle(command)) {
                try {
                    results.add(handler.invoke(command));
                }catch(Exception ex) {
                    printError(ex.getMessage());
                    if(printStacktrace)
                        ex.printStackTrace();
                }

                if(!multiHandleCommands)
                    break;
            }
        }
        return results.toArray(String[]::new);
    }

    public final Collection<String> getCommands() {
        return handlers.stream().flatMap(handler -> handler.getCommands().stream()).toList();
    }

}
