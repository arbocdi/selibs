package net.sf.selibs.tcp.links;

import net.sf.selibs.utils.chain.HException;
import net.sf.selibs.utils.chain.Handler;
import org.simpleframework.xml.Root;

@Root
public class BinaryEcho implements Handler<TCPMessage, Void> {

    @Override
    public Void handle(TCPMessage i) throws Exception {
        while (true) {
            int bt = i.in.read();
            if (bt == -1) {
                break;
            }
            i.out.write(bt);
            i.out.flush();
        }
        return null;
    }

}
