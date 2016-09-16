package net.sf.selibs.utils.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.sf.selibs.utils.graph.GraphUtils;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

@ToString
@EqualsAndHashCode
public class Injector {

    protected Bindings bindings = new Bindings();

    @Root
    @ToString
    @EqualsAndHashCode
    public static class Bindings {

        @Getter
        @ElementMap
        protected Map<String, Object> data = new ConcurrentHashMap();
    }

    public Object addBinding(Class clazz, Object instance) {
        return bindings.data.put(clazz.getName(), instance);
    }

    public Object addBinding(Class clazz, String name, Object instance) {
        return bindings.data.put(clazz.getName() + "." + name, instance);
    }

    public <T> T getBinding(Class<T> clazz) {
        return (T) this.bindings.data.get(clazz.getName());
    }

    public <T> T getBinding(Class<T> clazz, String name) {
        return (T) this.bindings.data.get(clazz.getName() + "." + name);
    }

    protected String getName(Field f) {
        String name = f.getType().getName();
        Named named = f.getAnnotation(Named.class);
        if (named != null) {
            name = name + "." + named.value();
        }

        return name;
    }

    public void injectIntoGraph(Object graph) throws IllegalArgumentException, IllegalAccessException {
        List objects = GraphUtils.graphToList(graph);
        for (Object o : objects) {
            this.injectInto(o);
        }
    }

    public void injectInto(Object obj) throws IllegalArgumentException, IllegalAccessException {
        for (Field f : GraphUtils.getAllFields(obj.getClass())) {
            Inject ann = f.getAnnotation(Inject.class);
            if (ann != null) {
                Object value = this.bindings.data.get(this.getName(f));
                if (value != null) {
                    f.setAccessible(true);
                    f.set(obj, value);
                }
            }
        }
    }

    //SE=============================
    public void injectIntoGraphSE(Object graph) throws IllegalArgumentException, IllegalAccessException {
        List objects = GraphUtils.graphToList(graph);
        for (Object o : objects) {
            this.injectIntoSE(o);
        }
    }

    public void injectIntoSE(Object obj) throws IllegalArgumentException, IllegalAccessException {
        for (Field f : GraphUtils.getAllFields(obj.getClass())) {
            InjectSE ann = f.getAnnotation(InjectSE.class);
            if (ann != null) {
                Object value = this.bindings.data.get(this.getNameSE(f));
                if (value != null) {
                    f.setAccessible(true);
                    f.set(obj, value);
                }
            }
        }
    }

    protected String getNameSE(Field f) {
        String name = f.getType().getName();
        NamedSE named = f.getAnnotation(NamedSE.class);
        if (named != null) {
            name = name + "." + named.value();
        }

        return name;
    }
}
