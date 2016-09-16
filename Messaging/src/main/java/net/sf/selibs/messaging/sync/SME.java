package net.sf.selibs.messaging.sync;

import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.MessageStatus;
import net.sf.selibs.messaging.sync.handlers.MessageHandler;
import org.simpleframework.xml.Root;

@Root
public class SME implements MessageHandler {

    protected final MQueue queue = new MQueue();
    protected final Object leftSync = new Object();
    


    public SME() {
        this.queue.close();
    }

    @Override
    public void setReady(boolean ready) {
        if (ready) {
            this.queue.open();
        } else {
            this.queue.close();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public Message exchange(Message request) throws Exception {
        synchronized (this.leftSync) {
            Message responce = null;
            try {
                this.queue.enqueue(request);
                responce = (Message) this.queue.dequeue();
            } catch (Exception ex) {
                this.queue.close();
                throw ex;
            }
            if (responce.status != MessageStatus.OK) {
                if (responce.payload instanceof Exception) {
                    throw (Exception) responce.payload;
                } else {
                    throw new Exception(responce.payload.toString());
                }
            }
            return responce;
        }
    }

   public Message getRequest() throws ClosedException, InterruptedException{
       return (Message) this.queue.dequeue();
   }
   public void setResponse(Message response) throws ClosedException, InterruptedException{
       this.queue.enqueue(response);
   }

}
