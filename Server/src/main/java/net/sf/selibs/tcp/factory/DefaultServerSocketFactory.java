package net.sf.selibs.tcp.factory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import org.simpleframework.xml.Root;
@Root
public class DefaultServerSocketFactory extends ServerSocketFactory{

    @Override
    public ServerSocket createServerSocket() throws IOException {
        ServerSocket socket = new ServerSocket();
        return socket;
    }
    
    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
