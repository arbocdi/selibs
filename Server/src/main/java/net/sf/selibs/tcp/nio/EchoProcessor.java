package net.sf.selibs.tcp.nio;

import net.sf.selibs.tcp.nio.module.Dispatcher;
import org.simpleframework.xml.Root;

@Root
public class EchoProcessor implements Processor {

    @Override
    public void process(byte[] data, Connection con, Dispatcher dis) {
        dis.addWriteRequest(con, data);
    }

}
