package net.sf.selibs.tcp.nio.module;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.Connection;
import org.simpleframework.xml.Root;
@Root
public class HConnector extends Connector {

    public static final String CFG_NAME = "nioConfig";

    @Override
    protected boolean makeConnection(SocketChannel sc, Connection con) throws IOException {
        TCPConfig cfg = (TCPConfig) con.getAttachments().get(CFG_NAME);
        return sc.connect(new InetSocketAddress(cfg.getIp(), cfg.getPort()));
    }
}
