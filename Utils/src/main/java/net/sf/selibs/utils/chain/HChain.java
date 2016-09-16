package net.sf.selibs.utils.chain;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import net.sf.selibs.utils.graph.GraphUtils;
import net.sf.selibs.utils.graph.Init;
import net.sf.selibs.utils.inject.Injector;

public class HChain {

    public static List<Handler> getHandlers(Handler link) {
        List<Handler> filters;
        if (!(link instanceof HLink)) {
            filters = new LinkedList();
            filters.add(link);
            return filters;
        }
        HLink hLink = (HLink) link;
        if (hLink.next != null) {
            filters = getHandlers(hLink.next);
        } else {
            filters = new LinkedList();
        }
        filters.add(0, link);
        return filters;
    }

    public static void initChain(Handler link) throws Exception {
        List objects = GraphUtils.graphToList(link);
        GraphUtils.invokeAnnotatedMethods(objects, Init.class);
    }

    public static void injectChain(Handler link, Injector inj) throws IllegalArgumentException, IllegalAccessException {
        inj.injectIntoGraph(link);
    }

    public static <T> T getHandler(Class<T> clazz, Handler link) {
        List<Handler> handlers = HChain.getHandlers(link);
        for (Handler h : handlers) {
            if (h.getClass() == clazz) {
                return (T) h;
            }
        }
        return null;
    }

    @Getter
    protected Handler first;
    @Getter
    protected Handler last;

    public void add(Handler h) {
        if (this.first == null) {
            first = h;
        } else {
            ((HLink) last).setNext(h);
        }
        last = h;
    }
}
