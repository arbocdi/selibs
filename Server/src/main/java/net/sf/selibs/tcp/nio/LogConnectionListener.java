package net.sf.selibs.tcp.nio;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.Root;



@Slf4j
@Root
public class LogConnectionListener implements ConnectionListener {

    @Override
    public void connected(Connection con) {
        try {
            log.debug(String.format("New connection established %s", con.getChannel().getRemoteAddress()));
        } catch (Exception ignore) {
        }
    }

    @Override
    public void disconnected(Connection con) {
        try {
            log.debug(String.format("Disonnected %s", con));
        } catch (Exception ignore) {
        }
    }

}
