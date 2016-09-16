package net.sf.selibs.tcp.nio.module;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.utils.misc.UHelper;
import net.sf.selibs.utils.service.Service;
import net.sf.selibs.utils.service.ServiceException;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Slf4j
@Root
public class Acceptor extends NService {

    protected ServerSocketChannel serverSocketChannel;
    
    public Acceptor(){
        
    }
    
    public Acceptor(TCPConfig cfg) {
        this.cfg = cfg;
    }

    @Override
    protected void doStuff() {
        SocketChannel sc = null;
        try {
            sc = this.serverSocketChannel.accept();
            sc.configureBlocking(false);
            Connection con = new Connection(sc);
            this.dispatcher.register(con);
        } catch (Exception ex) {
            log.debug("Cant accept client connection", ex);
            UHelper.close(sc);
        }
    }

    @Override
    protected synchronized void postAction() {
        this.unbind();
        this.dispatcher.stop();
    }

    @Override
    public void start() throws ServiceException {
        try {
            this.dispatcher.start();
            this.bind();
            super.start();
        } catch (Exception ex) {
            log.warn("Cant start acceptor", ex);
            this.stop();
            this.postAction();
            throw new ServiceException(ex);
        }
    }

    protected void bind() throws Exception {
        log.info(String.format("Binding server socket %s", cfg));
        serverSocketChannel = ServerSocketChannel.open();
        if (cfg.getIp() == null) {
            serverSocketChannel.bind(new InetSocketAddress(cfg.getPort()), cfg.getBacklog());
        } else {
            serverSocketChannel.bind(new InetSocketAddress(cfg.getIp(), cfg.getPort()), cfg.getBacklog());
        }

    }

    protected void unbind() {
        log.info(String.format("Unbinding NIOServer %s", cfg));
        UHelper.close(this.serverSocketChannel);
    }

}
