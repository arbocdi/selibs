package net.sf.selibs.messaging.sync.tcp;

import java.io.ObjectInputStream;
import javax.inject.Inject;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.sync.handlers.MessageHandler;
import net.sf.selibs.tcp.links.TCPMessage;
import net.sf.selibs.utils.chain.Handler;
import net.sf.selibs.utils.io.streams.OOSWrapper;
import org.simpleframework.xml.Root;

@Root
@Slf4j
@ToString
@Data
public class Responder implements Handler<TCPMessage, Void> {

    @Inject
    protected MessageHandler mh;

    protected void makeExchangeCycle(TCPMessage ctx) throws Exception {
        //request===========
        Message request = (Message) ((ObjectInputStream) ctx.in).readObject();
        //responce==========
        Message responce;
        try {
            responce = mh.exchange(request);
        } catch (Exception ex) {
            responce = request.createErrorResponce(ex);
        }
        ((OOSWrapper) ctx.out).writeObject(responce);
    }

    @Override
    public Void handle(TCPMessage i) throws Exception {
        mh.setReady(true);
        try {
            while (!Thread.currentThread().isInterrupted()) {
                this.makeExchangeCycle(i);
            }
        } finally {
            this.mh.setReady(false);
        }
        return null;
    }

}
