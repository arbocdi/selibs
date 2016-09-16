package net.sf.selibs.tcp.nio;

import org.simpleframework.xml.Root;

@Root
public interface ConnectionListener {
    public void connected(Connection con);
    public void disconnected(Connection con);
}
