package net.sf.selibs.utils.amq;

import javax.jms.Connection;
import net.sf.selibs.utils.misc.SyncCounter;

public class CountingCL implements ConnectionListener {

    public SyncCounter aquireCounter = new SyncCounter() ;
    public SyncCounter releaseCounter = new  SyncCounter();

    @Override
    public void connectionAquired(Connection con) {
        this.aquireCounter.increment();
    }

    @Override
    public void connectionReleased(Connection con) {
        this.releaseCounter.increment();
    }

}
