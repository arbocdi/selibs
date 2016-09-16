package net.sf.selibs.tcp.nio.http;

import net.sf.selibs.http.HMessage;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import org.simpleframework.xml.Root;

@Root
public interface HttpProcessor {
     public void process(Connection con, HMessage request, Dispatcher dis) throws Exception;
}
