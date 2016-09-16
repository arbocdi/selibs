package net.sf.selibs.utils.locator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceLocator {

    private static final Map<ServiceName, Object> services = new ConcurrentHashMap();

    public static Map getServices() {
        return services;
    }

    public static Object put(ServiceName k, Object v) {
        return services.put(k, v);
    }

    public static Object get(ServiceName k) {
        return services.get(k);
    }
}
