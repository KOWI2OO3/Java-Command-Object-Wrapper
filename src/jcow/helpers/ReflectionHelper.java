package jcow.helpers;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;

import jcow.utils.Pair;

public final class ReflectionHelper {
    
    private ReflectionHelper() {}

    /**
     * Gets all classes in a certain folder
     * and underlying folders that have the specified annotation over the class
     * as long as the annotation has a retention of runtime
     * @param packageName the name of the package containing the classes
     * @param annotation the annotation to check for to get the classes
     * @param <T> the type of the annotation
     * @return a list of pairs of classes and the annotation
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Annotation> List<Pair<Class, T>>
    getAnnotatedClasses(String packageName, Class<T> annotation) {
        return getClasses(packageName)
            .filter(type -> type.isAnnotationPresent(annotation))
            .map(type -> Pair.of(type, (T)type.getAnnotation(annotation)))
            .toList();
    }

    /**
     * Gets all classes in a certain
     * folder and underlying folders (Recursively)
     * @param packageName the name of the package containing the classes
     * @return a stream of all classes in a certain package and below
     */
    @SuppressWarnings("rawtypes")
    private static Stream<Class> getClasses(String packageName) {
        try {
            var classLoader = ReflectionHelper.class.getClassLoader();
            if(classLoader == null || packageName == null) return Stream.of();

            var path = ClassPath.from(classLoader);
            return path.getTopLevelClassesRecursive(packageName)
                .stream()
                .map(info -> getClassFromName(info.getName()))
                .filter(clazz -> clazz != null);
        } catch (IOException e) {
            System.err.println("Failed to load classes from invalid package {"
                    + packageName + "}");
            e.printStackTrace();
        }
        return Stream.of();
    }
    
    /**
     * Gets a class from its specified name
     * @param className the specified name of a class
     * @return the class of the specified name
     * Or null if no class should be found
     */
    @SuppressWarnings("rawtypes")
    private static Class getClassFromName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {}
        return null;
    }

    private static final Set<Class<?>> PRIMITIVE_NUMBER_TYPES = Set.of(int.class, byte.class, long.class, double.class, float.class, short.class);
    private static final Set<Class<?>> NUMBER_TYPES = Set.of(Integer.class, Byte.class, Long.class, Double.class, Float.class, Short.class);
    private static final Set<Class<?>> PRIMITIVE_TYPES = Sets.union(Sets.union(PRIMITIVE_NUMBER_TYPES, NUMBER_TYPES), Set.of(String.class, boolean.class, Boolean.class, char.class, Character.class));

    /**
     * Checks to see if the type supplied is a number based type
     * @return whether the type is a number
     */
    public static boolean isNumber(Class<?> type) {
        return NUMBER_TYPES.contains(type) || PRIMITIVE_NUMBER_TYPES.contains(type);
    }

    /**
     * Checks to see if the type supplied is a considered primitive.
     * This just means we check if its a primitive or a string
     * @return whether the type primitive and string
     */
    public static boolean isPrimitive(Class<?> type) {
        return PRIMITIVE_TYPES.contains(type);
    }

    /**
     * Tries to parse a number from the given input string
     * @param <T> the type of the number
     * @param input the input string to parse
     * @param resultType the type to parse to
     * @return the parsed number of the correct type
     */
    public static <T> Object parseNumber(String input, Class<T> resultType) {
        // Not my proudest piece of code, but honestly don't know how to do this as precise
        // without using libaries
        var number = Double.valueOf(input);
        if(resultType == Integer.class)
            return (Integer)number.intValue();
        if(resultType == int.class)
            return number.intValue();
        if(resultType == Byte.class)
            return (Byte)number.byteValue();
        if(resultType == byte.class)
            return number.byteValue();
        if(resultType == Long.class)
            return (Long)number.longValue();
        if(resultType == long.class)
            return number.longValue();
        if(resultType == Double.class)
            return (Double)number.doubleValue();
        if(resultType == double.class)
            return number.doubleValue();
        if(resultType == Float.class)
            return (Float)number.floatValue();
        if(resultType == float.class)
            return number.floatValue();
        if(resultType == Short.class)
            return (Short)number.shortValue();
        if(resultType == short.class)
            return number.shortValue();
        return null;
    }

    /**
     * Tries to parse the given string into the primitive type supplied
     * <br></br>
     * <b>Note</b>: in this case a String is considered a primitive 
     * @param input the input string to parse
     * @param type the type to parse to
     * @return the parsed primitive of the correct type
     */
    public static Object parsePrimitive(String input, Class<?> type) {
        if(isNumber(type))
            return parseNumber(input, type);
        if(type == boolean.class || type == Boolean.class)
            return Boolean.parseBoolean(input);
        if(type == char.class || type == Character.class)
            return input.charAt(0);
        if(type == String.class)
            return input;
        return null;
    }

    /**
     * Gets a field from a class with the specified name
     * @param type the type to get the field from
     * @param name the name of the field to get
     * @return the field with the specified name or null if no field is found
     */
    public static Field getField(Class<?> type, String name) {
        return getField(type, name, null, null);
    }

    /**
     * Gets a field from a class with the specified name and that satisfies the filter
     * @param type the type to get the field from
     * @param name the name of the field to get
     * @param filter the filter of the field to get
     * @param nameMapper the mapper for the name of a field, this may be null then the normal name of the field is used
     * @return the field with the specified name and satisfies the filter or null if no field is found
     */
    public static Field getField(Class<?> type, String name, Predicate<Field> filter, Function<Field, String> nameMapper) {
        Function<Field, String> mapper = nameMapper == null ? field -> field.getName() : nameMapper;
        Predicate<Field> finalFilter = field -> 
            mapper.apply(field).equals(name) && (filter == null ? true : filter.test(field));

        var fields = getField(type, finalFilter);
        return fields.length == 0 ? null : fields[0];
    }

    /**
     * Gets a all fields from a class which satisfy the filter
     * @param type the type to get the field from
     * @param filter the filter of the field to get
     * @return the fields that satisfy the filter or and empty array if no fields has been found
     */
    public static Field[] getField(Class<?> type, Predicate<Field> filter) {
        if(type == null) return null;
        filter = filter == null ? field -> true : filter;
        return Arrays.stream(type.getDeclaredFields())
            .filter(filter)
            .toArray(Field[]::new);
    }

    /**
     * Gets a method from a class with the specified name
     * @param type the type to get the method from
     * @param name the name of the method to get
     * @return the method with the specified name or null if no method is found
     */
    public static Method getMethod(Class<?> type, String name) {
        if(type == null || name == null) return null;
        return getMethod(type, name, null, null);
    }

    /**
     * Gets a method from a class with the specified name
     * @param type the type to get the method from
     * @param name the name of the method to get
     * @param filter the filter of the method to get
     * @param nameMapper the mapper for the name of a method, this may be null then the normal name of the method is used
     * @return the method with the specified name and satisfies the filter or null if no method is found
     */
    public static Method getMethod(Class<?> type, String name, Predicate<Method> filter, Function<Method, String> nameMapper) {
        if(type == null || name == null) return null;
        Function<Method, String> mapper = nameMapper == null ? method -> method.getName() : nameMapper;
        return Arrays.stream(getMethod(type, filter))
            .filter(method -> mapper.apply(method).equals(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets a method from a class with the specified name
     * @param type the type to get the method from
     * @param filter the filter of the method to get
     * @return the methods that satisfy the filter or and empty array if no methods has been found
     */
    public static Method[] getMethod(Class<?> type, Predicate<Method> filter) {
        if(type == null ) return null;
        filter = filter == null ? method -> true : filter;
        return Arrays.stream(type.getDeclaredMethods())
            .filter(filter)
            .toArray(Method[]::new);
    }

    /**
     * Gets the length of the path to the method in the class
     * @param type the type to get the method from
     * @param path the path to the method
     * @param filter the filter for wich the method and the fields leading to it should satisfy
     * @param nameMapper the mapper for the name of a member, this may be null then the normal name of the member is used
     * @return the length of the path to the method where each element in the path satisfies the filter or -1 if no method is found
     */
    public static int getPathLength(Class<?> type, String[] path, Predicate<AccessibleObject> filter, Function<AccessibleObject, String> nameMapper) {
        for (int i = 0; i < path.length; i++) {
            var current = path[i];

            // if a method exists then we can just get the method and return it
            var method = getMethod(type, current, member -> filter.apply(member), member -> nameMapper.apply(member));
            if(method != null)
                return i;
            
            // if no method is found then we try to get the field such that we can find the method in the field
            var field = getField(type, current, member -> filter.apply(member), member -> nameMapper.apply(member));
            if(field == null)
                return -1;
            type = field.getType();
        }
        return -1;
    }

    /**
     * Gets a method from a class with the specified path, this path is a path through the fields of the class given
     * @param type the type to get the method from
     * @param path the path to the method
     * @param filter the filter for wich the method and the fields leading to it should satisfy
     * @param nameMapper the mapper for the name of a member, this may be null then the normal name of the member is used
     * @return the method with the specified path where each element in the path satisfies the filter or null if no method is found
     */
    public static Method getMethod(Class<?> type, String[] path, Predicate<AccessibleObject> filter, Function<AccessibleObject, String> nameMapper) {
        for (int i = 0; i < path.length; i++) {
            var current = path[i];

            // if a method exists then we can just get the method and return it
            var method = getMethod(type, current, member -> filter.apply(member), member -> nameMapper.apply(member));
            if(method != null)
                return method;
            
            // if no method is found then we try to get the field such that we can find the method in the field
            var field = getField(type, current, member -> filter.apply(member), member -> nameMapper.apply(member));
            if(field == null)
                return null;
            type = field.getType();
        }
        return null;
    }
}
