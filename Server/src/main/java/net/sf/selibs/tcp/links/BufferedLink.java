package net.sf.selibs.tcp.links;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import net.sf.selibs.utils.chain.HException;
import net.sf.selibs.utils.chain.HLink;
import org.simpleframework.xml.Root;

@Root
public class BufferedLink extends HLink<TCPMessage, Void> {

    @Override
    public Void handle(TCPMessage i) throws Exception {
        i.in = new BufferedInputStream(i.in);
        i.out = new BufferedOutputStream(i.out);
        return this.next.handle(i);
    }
    
}
