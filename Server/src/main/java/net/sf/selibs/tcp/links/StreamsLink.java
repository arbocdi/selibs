package net.sf.selibs.tcp.links;

import net.sf.selibs.utils.chain.HException;
import net.sf.selibs.utils.chain.HLink;
import org.simpleframework.xml.Root;

@Root
public class StreamsLink extends HLink<TCPMessage, Void> {

    @Override
    public Void handle(TCPMessage i) throws Exception {
        i.in = i.socket.getInputStream();
        i.out = i.socket.getOutputStream();
        return this.next.handle(i);
    }

   

}
