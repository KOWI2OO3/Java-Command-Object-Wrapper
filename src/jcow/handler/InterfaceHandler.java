package jcow.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * An basis implementation of an interface which should be extended to add the actual unique
 * interaction.
 * 
 * @author KOWI2003
 */
public abstract class InterfaceHandler {
    
    private final Thread thread;
    private final Set<CommandHandler> handlers;

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
     * Attaches a command handler such that it can be used to handle incomming commands
     * @param handler the handler to attach [not null]
     * @return whether the handler has been attached
     */
    public boolean attachHandler(CommandHandler handler) {
        if(handler == null)
            return false;

        return handlers.add(handler);
    }

    /**
     * Dettaches a command handler from the interface
     * @param handler the handler to detach 
     * @return whether the handler has been dettached, note: it will return false if the handler wasn't attached
     */
    public boolean dettachHandler(CommandHandler handler) {
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

    /**
     * Prints an error to the output
     * @param error the error to print
     */
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

}
