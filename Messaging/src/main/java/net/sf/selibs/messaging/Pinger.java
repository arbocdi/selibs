package net.sf.selibs.messaging;

import javax.inject.Inject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.utils.service.Service;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
@Data
@Slf4j
public class Pinger extends Service {
    @Inject
    @Setter@Getter
    @Element(required=false)
    protected MessageExchanger me;
    @Element
    protected String destination = "echo";
    @Element 
    protected String source="pinger";

    public Pinger(@Element(name = "sleepInterval") int sleepInterval) {
        this.setSleepInterval(sleepInterval);
    }
    public Pinger(){
        
    }

    @Override
    protected void doStuff() {
        Message request = new Message();
        request.destination = this.destination;
        request.source=this.source;
        request.status = MessageStatus.OK;
        try {
            me.exchange(request);
        } catch (Exception ex) {
            log.warn("Ping failed!", ex);
        }
    }

}
