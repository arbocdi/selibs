package net.sf.selibs.utils.amq;

import javax.jms.Message;

public interface  ResponseProducer extends RequestProducer{
    void setRequest(Message request);
    Message getRequest();
}
