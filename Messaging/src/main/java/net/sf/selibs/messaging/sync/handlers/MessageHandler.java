package net.sf.selibs.messaging.sync.handlers;

import net.sf.selibs.messaging.MessageExchanger;
import org.simpleframework.xml.Root;

@Root
public interface MessageHandler extends MessageExchanger {

    void setReady(boolean ready);

    void init();
}
