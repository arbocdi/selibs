package net.sf.selibs.tcp.links;

import net.sf.selibs.utils.chain.Handler;
import org.simpleframework.xml.Root;
@Root
public class StubHandler implements Handler<TCPMessage,Void>{

    @Override
    public Void handle(TCPMessage i) throws Exception {
        return null;
    }

   
    
}
