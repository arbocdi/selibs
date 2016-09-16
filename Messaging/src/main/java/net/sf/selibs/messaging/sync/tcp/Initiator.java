package net.sf.selibs.messaging.sync.tcp;

import java.io.ObjectInputStream;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.sync.ClosedException;
import net.sf.selibs.messaging.sync.SME;
import net.sf.selibs.tcp.links.TCPMessage;
import net.sf.selibs.utils.chain.Handler;
import net.sf.selibs.utils.io.streams.OOSWrapper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@ToString
@Slf4j
public class Initiator implements Handler<TCPMessage, Void> {

    @Inject
    @Setter
    @Getter
    @Element(required = false)
    protected SME me;

    protected void makeExchangeCycle(TCPMessage ctx, SME me) throws Exception {
        me.setReady(true);
        try {
            ((OOSWrapper) ctx.out).writeObject(me.getRequest());
            me.setResponse((Message) ((ObjectInputStream) ctx.in).readObject());
        } catch (ClosedException ex) {
        } catch (Exception ex) {
            me.setReady(false);
            throw ex;
        }
    }

    @Override
    public synchronized Void handle(TCPMessage i) throws Exception {
        while (!Thread.currentThread().isInterrupted()) {
            this.makeExchangeCycle(i, me);
        }
        return null;
    }

}
