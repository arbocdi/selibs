package net.sf.selibs.messaging.sync.handlers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.ToString;
import net.sf.selibs.messaging.Message;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

@Root
@ToString
public class HandlerMap implements MessageHandler {

    @ElementMap
    protected Map<String, MessageHandler> handlers = new ConcurrentHashMap();

    public MessageHandler addHandler(MessageHandler handler, String name) {
        return this.handlers.put(name, handler);
    }

    public MessageHandler removeHandler(String name) {
        return this.handlers.remove(name);
    }

    @Override
    public Message exchange(Message request) throws Exception {
        MessageHandler handler = this.handlers.get(request.destination);
        return handler.exchange(request);
    }

    @Override
    public void init() {
        for (MessageHandler handler : this.handlers.values()) {
            handler.init();
        }
    }

    @Override
    public void setReady(boolean ready) {
    }

}
