package net.sf.selibs.tcp.nio.module;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.Utils;
import net.sf.selibs.utils.misc.UHelper;
import net.sf.selibs.utils.service.ServiceException;
import org.simpleframework.xml.Root;

@Root
@Slf4j
public class Connector extends NService {

    //work================
    protected Selector selector;
    protected final Object guard = new Object();

    public Connector() {
    }

    public Connector(TCPConfig cfg) {
        this.cfg = cfg;
    }

    @Override
    protected void doStuff() {
        synchronized (guard) {
        }
        try {
            this.selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keysI = keys.iterator();
            this.processKeys(keysI);
        } catch (Exception ex) {
            log.debug("Error while selecting", ex);
        }
    }

    protected void processKeys(Iterator<SelectionKey> keys) {
        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            keys.remove();
            if (key.isValid() && key.isConnectable()) {
                this.connect(key);
            }
        }
    }

    protected void connect(SelectionKey key) {
        Connection con = (Connection) key.attachment();
        try {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            if (clientChannel.finishConnect()) {
                key.cancel();
                this.dispatcher.register(con);
            }
        } catch (Exception ex) {
            log.debug(String.format("Error while connecting to %s,closing", con), ex);
            this.close(con);
        }
    }

    public void addConnectRequest(Connection con) throws Exception {
        //System.out.println("Adding connect request "+con);
        synchronized (guard) {
            this.dispatcher.putConnection(con);
            this.selector.wakeup();
            SocketChannel sc = null;
            SelectionKey key = null;
            try {
                sc = SocketChannel.open();
                con.setChannel(sc);
                sc.configureBlocking(false);
                key = sc.register(selector, SelectionKey.OP_CONNECT);
                key.attach(con);
                boolean result = this.makeConnection(sc, con);//sc.connect(new InetSocketAddress(cfg.getIp(), cfg.getPort()));
                if (result) {
                    this.connect(key);
                }
            } catch (Exception ex) {
                log.debug(String.format("Error while connecting to %s,closing", con), ex);
                this.close(con);
                throw ex;
            }
        }

    }

    protected boolean makeConnection(SocketChannel sc, Connection con) throws IOException {
        return sc.connect(new InetSocketAddress(cfg.getIp(), cfg.getPort()));
    }

    @Override
    public void start() throws ServiceException {
        try {
            this.selector = Selector.open();
            this.dispatcher.start();
            super.start();
        } catch (Exception ex) {
            this.postAction();
            this.stop();
            throw new ServiceException(ex);
        }

    }

    public void close(Connection con) {
        synchronized (guard) {
            Utils.close(con, selector);
        }
        this.dispatcher.close(con);
    }

    public void closeAllConnections() {
        synchronized (guard) {
            this.dispatcher.closeAllConnections();
            Utils.closeAllConnections(selector);
        }
    }

    @Override
    protected synchronized void postAction() {
        this.dispatcher.stop();
        this.closeAllConnections();
        UHelper.close(selector);
    }

}
