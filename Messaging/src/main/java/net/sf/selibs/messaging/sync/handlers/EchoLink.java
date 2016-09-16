package net.sf.selibs.messaging.sync.handlers;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.messaging.Message;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class EchoLink extends MHandlerChain {

    @Element
    @Getter
    @Setter
    protected String echo = "echoResponder";

    @Override
    public Message exchange(Message request) throws Exception {
        if (request.destination.equals(echo)) {
            Message response = request.createOKResponce();
            response.payload = request.payload;
            return response;
        }
        return this.next.exchange(request);
    }

}
