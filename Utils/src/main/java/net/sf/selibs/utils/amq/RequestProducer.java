package net.sf.selibs.utils.amq;

import javax.jms.Message;
import javax.jms.Session;

public interface RequestProducer {
    Message produce(Session session) throws Exception;
}
