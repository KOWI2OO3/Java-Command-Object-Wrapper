package jcow.helpers;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.reflect.ClassPath;

import jcow.utils.Pair;

public final class ReflectionHelper {
    
    private ReflectionHelper() {};

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

}
