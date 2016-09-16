package net.sf.selibs.utils.amq;

import java.util.Enumeration;
import javax.jms.Message;
import javax.jms.Session;

public class EchoResponseProducer implements ResponseProducer {

    protected Message request;

    @Override
    public Message produce(Session session) throws Exception {
        Message response = session.createMessage();
        Enumeration<String> props = request.getPropertyNames();
        while (props.hasMoreElements()) {
            String propName = props.nextElement();
            response.setObjectProperty(propName, request.getObjectProperty(propName));
        }
        return response;
    }

    @Override
    public void setRequest(Message request) {
        this.request = request;
    }

    @Override
    public Message getRequest() {
        return this.request;
    }

}
