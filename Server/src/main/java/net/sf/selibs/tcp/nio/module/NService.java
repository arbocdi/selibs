package net.sf.selibs.tcp.nio.module;

import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.utils.graph.Node;
import net.sf.selibs.utils.service.Service;
import org.simpleframework.xml.Element;

public abstract  class NService extends Service {
    @Setter
    @Getter
    @Element(name = "config",required = false)
    protected TCPConfig cfg;
    @Setter
    @Getter
    @Element(name = "dispatcher")
    @Node
    protected Dispatcher dispatcher;
    
    @Override
    public void join() throws InterruptedException {
        super.join();
        this.dispatcher.join();
    }
}

