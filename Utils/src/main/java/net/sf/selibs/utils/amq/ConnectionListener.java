package net.sf.selibs.utils.amq;

import javax.jms.Connection;

public interface ConnectionListener {
    void connectionAquired(Connection con);
    void connectionReleased(Connection con);
}
