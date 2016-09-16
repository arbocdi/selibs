package net.sf.selibs.utils.misc;

import java.lang.reflect.Method;

public class MethodInvoker {

    public static Object invoke(Object obj, String name, Object[] parameters) throws Exception {
        Method m = obj.getClass().getMethod(name, getClasses(parameters));
        return m.invoke(obj, parameters);
    }

    public static Object invoke(Object obj, String name, Class[] classes, Object[] params) throws Exception {
        if (classes.length == 0) {
            Method m = obj.getClass().getMethod(name);
            return m.invoke(obj);
        } else {
            Method m = obj.getClass().getMethod(name, classes);
            return m.invoke(obj, params);
        }
    }

    public static Class[] getClasses(Object... parameters) {
        Class[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            classes[i] = parameters[i].getClass();
        }
        return classes;
    }
}
