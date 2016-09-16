package net.sf.selibs.tcp.nio;

import lombok.Data;
import net.sf.selibs.utils.misc.SyncCounter;

/**
 *
 * @author root
 */
@Data
public class CountingConnectionListener implements ConnectionListener {

    public SyncCounter connected = new SyncCounter();
    public SyncCounter disconnected = new SyncCounter();
    
    @Override
    public void connected(Connection con) {
        this.connected.increment();
    }

    @Override
    public void disconnected(Connection con) {
        this.disconnected.increment();
    }
    
}
