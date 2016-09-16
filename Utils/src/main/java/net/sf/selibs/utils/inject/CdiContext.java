package net.sf.selibs.utils.inject;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class CdiContext {

    public static CdiContext INSTANCE;

    public synchronized static void initialize() {
        if (INSTANCE == null) {
            INSTANCE = new CdiContext();
        }
    }

    public synchronized static void close() {
        if (INSTANCE != null) {
            INSTANCE.weld.shutdown();
        }
    }

    private final Weld weld;
    private final WeldContainer container;

    private CdiContext() {
        this.weld = new Weld();
        this.container = weld.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                weld.shutdown();
            }
        });
    }

    public <T> T getBean(Class<T> type) {
        return container.instance().select(type).get();
    }

    public void injectAndConstruct(Object aInstance) {

        BeanManager theManager = this.container.getBeanManager();

        AnnotatedType<Object> theType = (AnnotatedType<Object>) theManager.createAnnotatedType(aInstance.getClass());
        InjectionTarget<Object> theTarget = theManager.createInjectionTarget(theType);

        CreationalContext<Object> cc = theManager.createCreationalContext(null);

        theTarget.inject(aInstance, cc);
        theTarget.postConstruct(aInstance);
    }
}
