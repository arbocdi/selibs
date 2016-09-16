package net.sf.selibs.messaging.sync.handlers;

import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.sync.SME;
import net.sf.selibs.utils.service.Service;

@Slf4j
public class LocalConnector extends Service {
    
    public SME me;
    public MessageHandler mh;
    
    @Override
    protected void doStuff() {
        try {
            me.setReady(true);
            Message request = me.getRequest();
            try {
                Message response = mh.exchange(request);
                me.setResponse(response);
            } catch (Exception ex) {
                me.setResponse(request.createErrorResponce(ex));
            }
        } catch (Exception ex) {
            log.warn("Cant make exchange cycle", ex);
        }
    }
    
    @Override
    protected void postAction() {
        me.setReady(false);
    }
    
}
