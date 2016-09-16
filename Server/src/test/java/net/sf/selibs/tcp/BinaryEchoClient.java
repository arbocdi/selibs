package net.sf.selibs.tcp;

import junit.framework.Assert;
import net.sf.selibs.tcp.links.TCPMessage;
import net.sf.selibs.utils.chain.Handler;
import org.simpleframework.xml.Root;

@Root
public class BinaryEchoClient implements Handler<TCPMessage, Void> {

    @Override
    public Void handle(TCPMessage msg) throws Exception {
        for (int i = 0; i < 200; i++) {
            msg.out.write(i);
            msg.out.flush();
            Assert.assertEquals((byte) i, (byte) msg.in.read());
        }
        return null;
    }

    

}
