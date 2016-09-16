package net.sf.selibs.utils.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.utils.cloning.NotCloneable;
import net.sf.selibs.utils.graph.GraphUtils;

@Slf4j
public class UHelper {

    public static void startThreads(List<Thread> threads) {
        for (Thread t : threads) {
            t.start();
        }
    }

    public static void joinThreads(List<Thread> threads) throws InterruptedException {
        for (Thread t : threads) {
            t.join();
        }
    }

    public static void close(AutoCloseable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ex) {

            }
        }
    }

    public static void close(Object o) {
        callNoArgMethod(o, "close");
    }

    public static void rollback(Object o) {
        callNoArgMethod(o, "rollback");
    }

    public static String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    public static void callNoArgMethod(Object o, String method) {
        if (o != null) {
            try {
                Method m = o.getClass().getMethod(method);
                if (m != null) {
                    m.invoke(o);
                }
            } catch (Exception ex) {
                log.warn(String.format("Cant call method %s", method), ex);
            }
        }

    }

    public static <T> T cloneReflection(T o) throws InstantiationException, IllegalAccessException {
        T clone = (T) o.getClass().newInstance();
        List<Field> fields = GraphUtils.getAllFields(o.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(NotCloneable.class) == null) {
                field.set(clone, field.get(o));
            }
        }
        return clone;
    }

    public static <T extends Serializable> T cloneSerializeation(T o) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        ObjectOutputStream oos = null;
        ObjectInputStream oin = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bout);
            oos.writeObject(o);
            oos.flush();
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            oin = new ObjectInputStream(bin);
            return (T) oin.readObject();
        } finally {
            UHelper.close(oos);
            UHelper.close(oin);
        }
    }

    public static <T> T lookupInJNDI(String name) throws NamingException {
        Context ctx = null;
        try {
            ctx = new InitialContext();
            return (T) ctx.lookup(name);
        } finally {
            UHelper.close(ctx);
        }
    }
    
}
