package net.sf.selibs.tcp.nio.module;

import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.ConnectionListener;
import net.sf.selibs.tcp.nio.Processor;
import net.sf.selibs.utils.graph.GraphUtils;
import net.sf.selibs.utils.graph.Init;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.Initializable;
import net.sf.selibs.utils.service.AbstractService;
import net.sf.selibs.utils.service.ServiceException;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class NModule implements AbstractService, Initializable<Injector> {
    
    @Setter
    @Getter
    @Element(name = "service")
    protected NService service;
    
    public NModule(@Element(name = "service") NService service) {
        this.service = service;
    }
    
    public void setConnectionListener(ConnectionListener listener) {
        service.getDispatcher().setListener(listener);
    }
    
    public ConnectionListener getConnectionListener() {
        return service.getDispatcher().getListener();
    }
    
    public void setProcessor(Processor proc) {
        service.getDispatcher().setProcessor(proc);
    }
    
    public Processor getProcessor() {
        return this.service.getDispatcher().getProcessor();
    }
    
    public void setConfig(TCPConfig cfg) {
        this.service.setCfg(cfg);
    }
    
    public TCPConfig getConfig() {
        return service.getCfg();
    }
    
    @Override
    public void start() throws ServiceException {
        try {
            this.service.start();
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }
    
    @Override
    public void stop() {
        this.service.stop();
    }
    
    @Override
    public void join() throws InterruptedException {
        this.service.join();
    }
    
    public static NModule generateServer(TCPConfig cfg) {
        Acceptor acc = new Acceptor(cfg);
        acc.dispatcher = new Dispatcher();
        return new NModule(acc);
    }
    
    public static NModule generateClient(TCPConfig cfg) {
        Connector acc = new Connector(cfg);
        acc.dispatcher = new Dispatcher();
        return new NModule(acc);
    }
    
    public static NModule generateClient() {
        HConnector acc = new HConnector();
        acc.dispatcher = new Dispatcher();
        return new NModule(acc);
    }
    
    @Override
    public void init() throws Exception {
        GraphUtils.invokeAnnotatedMethods(this.service, Init.class);
    }
    
    @Override
    public void inject(Injector injector) throws Exception {
        injector.injectIntoGraph(this.service);
    }
    
}
