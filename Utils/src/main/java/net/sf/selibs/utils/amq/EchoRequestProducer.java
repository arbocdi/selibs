package net.sf.selibs.utils.amq;

import javax.jms.Message;
import javax.jms.Session;

public class EchoRequestProducer implements RequestProducer {

    public static final String PROP_NAME = "echoTest";
    public String propValue;

    @Override
    public Message produce(Session session) throws Exception {
        Message request = session.createMessage();
        request.setStringProperty(PROP_NAME, propValue);
        return request;
    }

}
