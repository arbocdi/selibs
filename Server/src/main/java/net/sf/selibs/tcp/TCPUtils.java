package net.sf.selibs.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import net.sf.selibs.utils.misc.UHelper;

public class TCPUtils {

    public static void bindServerSocket(ServerSocket socket, TCPConfig cfg) throws IOException {
        try {
            if (cfg.ip != null) {
                socket.bind(new InetSocketAddress(cfg.ip, cfg.port), cfg.backlog);
            } else {
                socket.bind(new InetSocketAddress(cfg.port), cfg.backlog);
            }
            if (cfg.timeout != null) {
                socket.setSoTimeout(cfg.timeout);
            }
        } catch (IOException ex) {
            UHelper.close(socket);
            throw ex;
        }
    }

    public static void connectSocket(Socket socket, TCPConfig cfg) throws IOException {
        try {
            if (cfg.timeout != null) {
                socket.connect(new InetSocketAddress(cfg.ip, cfg.port), cfg.timeout);
                socket.setSoTimeout(cfg.timeout);
            } else {
                socket.connect(new InetSocketAddress(cfg.ip, cfg.port));
            }
        } catch (IOException ex) {
            UHelper.close(socket);
            throw ex;
        }
    }

}
