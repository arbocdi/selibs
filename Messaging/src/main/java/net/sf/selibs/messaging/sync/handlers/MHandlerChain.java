package net.sf.selibs.messaging.sync.handlers;

import lombok.Data;

@Data
public abstract class MHandlerChain implements MessageHandler{
    
    protected MessageHandler next;
    
    @Override
    public void setReady(boolean ready) {
        if (this.next!=null){
            next.setReady(ready);
        }
    }

    @Override
    public void init() {
    }

    
    
}
