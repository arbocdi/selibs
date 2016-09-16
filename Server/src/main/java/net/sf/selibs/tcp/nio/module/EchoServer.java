package net.sf.selibs.tcp.nio.module;

import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.CountingConnectionListener;
import net.sf.selibs.tcp.nio.EchoProcessor;
import net.sf.selibs.tcp.nio.LogConnectionListener;
import net.sf.selibs.tcp.nio.http.HEchoProcessor;
import net.sf.selibs.tcp.nio.http.HttpBridge;
import org.simpleframework.xml.Root;

@Root
public class EchoServer {

    public static NModule generateHttpEcho(TCPConfig cfg) {
        NModule server = NModule.generateServer(cfg);
        server.getService().getDispatcher().setListener(new CountingConnectionListener());

        HttpBridge bridge = new HttpBridge();
        bridge.httpProc = new HEchoProcessor();

        server.setProcessor(bridge);
        return server;
    }
    public static NModule generateBinaryEcho(TCPConfig cfg) {
        NModule server = NModule.generateServer(cfg);
        server.getService().getDispatcher().setListener(new CountingConnectionListener());

        server.setProcessor(new EchoProcessor());
        return server;
    }

}
