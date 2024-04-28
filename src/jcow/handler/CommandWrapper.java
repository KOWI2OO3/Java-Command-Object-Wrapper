package jcow.handler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import jcow.command.ICommand;
import jcow.command.IContext;
import jcow.command.annotations.Controller;
import jcow.helpers.ReflectionHelper;
import jcow.utils.ParameterReader;

/**
 * A wrapper for a command that will be invoked using reflection.
 * This class is used to wrap command registered as types using annotations and reflection to handle the command as any other command.
 * 
 * @author KOWI2003
 */
public class CommandWrapper implements ICommand {
    
    private Class<?> type;

    public CommandWrapper(Class<?> type) {
        this.type = type;
    }

    @Override
    public String invoke(IContext context) {
        int lastPathIndex = ReflectionHelper.getPathLength(type, context.getParameters(), this::hasAnnotation, this::getMemberName) + 1;
        var method = ReflectionHelper.getMethod(type, context.getParameters(), this::hasAnnotation, this::getMemberName);
        if (method == null)
            return "Command not found";
            
        var length = context.getParameters().length - lastPathIndex;
        var parameters = new String[length];
        System.arraycopy(context.getParameters(), lastPathIndex, parameters, 0, length);
        context.setParameters(parameters);
        
        return invokeMethod(method, context).toString();
    }

    @Override
    public Collection<String> nextCompletion(String context) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextCompletion'");
    }

    private boolean hasAnnotation(AccessibleObject member) {
        return member.isAnnotationPresent(Controller.class);
    }

    private String getMemberName(AccessibleObject member) {
        var controller = member.getAnnotation(Controller.class);
        if(controller != null && !controller.value().isEmpty())
            return controller.value();

        return switch (member) {
            case Field field -> field.getName();
            case Method method -> method.getName();
            default -> member.toString();
        };
    }

    /**
     * Tries to invoke a method with the specified name and parameters
     * @param method the method to invoke
     * @param params the parameters to pass to the method, that will be parsed according to the method's signature
     * @return the result of the method invocation
     */
    private Object invokeMethod(Method method, IContext context) {
        var types = method.getParameterTypes();
        var annotations = method.getParameterAnnotations();
        
        var parameterReader = new ParameterReader(context);
        var arguments = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            if(parameterReader.hasNext())
                arguments[i] = parameterReader.read(types[i], annotations[i]);
        }

        // Invokes the method with the parsed arguments
        try {
            return method.invoke(null, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
