package net.sf.selibs.tcp;

import javax.net.SocketFactory;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.tcp.factory.DefaultSocketFactory;
import net.sf.selibs.tcp.links.TCPMessage;
import net.sf.selibs.utils.chain.HChain;
import net.sf.selibs.utils.chain.Handler;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.misc.Initializable;
import net.sf.selibs.utils.misc.UHelper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class TCPClient implements Initializable<Injector>{

    //config=============
    @Element(name = "cfg")
    @Setter
    @Getter
    protected TCPConfig cfg;
    @Element(name = "handler")
    @Setter
    @Getter
    protected Handler<TCPMessage, Void> handler;
    @Element
    @Setter
    @Getter
    protected SocketFactory sf = new DefaultSocketFactory();
    //work================

    @Getter
    protected TCPMessage msg;

    public TCPClient(@Element(name = "cfg") TCPConfig cfg,
            @Element(name = "handler") Handler<TCPMessage, Void> handler) {
        this.cfg = cfg;
        this.handler = handler;
    }

    @Override
    public void init() throws Exception {
        HChain.initChain(handler);
    }

    @Override
    public void inject(Injector injector) throws Exception {
        HChain.injectChain(handler, injector);
    }

    public void handle() throws Exception {
        msg = new TCPMessage();
        msg.socket = sf.createSocket();
        TCPUtils.connectSocket(msg.socket, cfg);
        handler.handle(msg);
    }

    public void close() {
        UHelper.close(msg.out);
        UHelper.close(msg.in);
        UHelper.close(msg.socket);
    }
}
