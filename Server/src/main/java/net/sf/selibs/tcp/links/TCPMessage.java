package net.sf.selibs.tcp.links;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class TCPMessage {
    public InputStream in;
    public OutputStream out;
    public Socket socket;
    public Map<String,Object> attachements = new HashMap();
    
    public TCPMessage(){
        
    }
    public TCPMessage(Socket socket){
        this.socket = socket;
    }
}
