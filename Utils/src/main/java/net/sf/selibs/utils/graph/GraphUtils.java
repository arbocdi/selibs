package net.sf.selibs.utils.graph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphUtils {

    public static DistinctList graphToList(Object o) throws IllegalArgumentException, IllegalAccessException {
        DistinctList objects = new DistinctList();
        GraphUtils.graphToList(o, objects);
        return objects;
    }

    public static void graphToList(Object o, List objects) throws IllegalArgumentException, IllegalAccessException {
        if (o == null || objects.contains(o)) {
            return;
        }
        objects.add(o);
        for (Field f : getAllFields(o.getClass())) {
            Node tn = f.getAnnotation(Node.class);
            f.setAccessible(true);
            if (tn != null) {
                Class fieldType = f.getType();
                if (Collection.class.isAssignableFrom(fieldType)) {
                    Collection coll = (Collection) f.get(o);
                    for (Object obj : coll) {
                        graphToList(obj, objects);
                    }
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    Map map = (Map) f.get(o);
                    for (Object obj : map.values()) {
                        graphToList(obj, objects);
                    }
                } else {
                    graphToList(f.get(o), objects);
                }
            }
        }
    }

    public static void invokeAnnotatedMethods(Collection col, Class<? extends Annotation> annClass) throws Exception {
        for (Object o : col) {
            for (Method m : getAnnotatedMethods(o.getClass(), annClass)) {
                m.setAccessible(true);
                m.invoke(o);
            }
        }

    }

    public static void invokeAnnotatedMethods(Object col, Class<? extends Annotation> annClass) throws Exception {
        for (Object o : graphToList(col)) {
            for (Method m : getAnnotatedMethods(o.getClass(), annClass)) {
                m.setAccessible(true);
                m.invoke(o);
            }
        }

    }

    public static List<Method> getAnnotatedMethods(Class clazz, Class<? extends Annotation> annClass) {
        List<Method> methods = new LinkedList();
        for (Method m : getAllMethods(clazz)) {
            if (m.getAnnotation(annClass) != null) {
                methods.add(m);
            }
        }
        return methods;
    }

    public static List<Field> getAllFields(Class<?> type) {

        List<Field> fields;

        if (type.getSuperclass() != null) {
            fields = getAllFields(type.getSuperclass());
        } else {
            fields = new LinkedList();
        }
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        return fields;
    }

    public static List<Method> getAllMethods(Class<?> type) {

        List<Method> methods;

        if (type.getSuperclass() != null) {
            methods = getAllMethods(type.getSuperclass());
        } else {
            methods = new LinkedList();
        }
        methods.addAll(Arrays.asList(type.getDeclaredMethods()));

        return methods;
    }

    public static <T> T getFirstFromGraph(Object graph, Class<T> clazz) throws IllegalArgumentException, IllegalAccessException {
        List<T> list = getAllFromGraph(graph, clazz);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static <T> List<T> getAllFromGraph(Object graph, Class<T> clazz) throws IllegalArgumentException, IllegalAccessException {
        List<T> list = new LinkedList();
        for (Object o : GraphUtils.graphToList(graph)) {
            if (o.getClass() == clazz) {
                list.add((T) o);
            }
        }
        return list;

    }

}
