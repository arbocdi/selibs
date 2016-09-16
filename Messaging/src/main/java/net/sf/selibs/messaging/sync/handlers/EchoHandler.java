package net.sf.selibs.messaging.sync.handlers;

import net.sf.selibs.messaging.Message;
import org.simpleframework.xml.Root;
@Root
public class EchoHandler implements MessageHandler {

    @Override
    public Message exchange(Message request) throws Exception{
        Message responce = request.createOKResponce();
        responce.payload = request.payload;
        return responce;
    }

    @Override
    public void init() {
    }

    @Override
    public void setReady(boolean ready) {
    }

    

}
