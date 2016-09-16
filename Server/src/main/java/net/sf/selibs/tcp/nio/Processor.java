package net.sf.selibs.tcp.nio;

import net.sf.selibs.tcp.nio.module.Dispatcher;
import org.simpleframework.xml.Root;

@Root
public interface Processor {
    public void process(byte[] data,Connection con,Dispatcher dis);
}
