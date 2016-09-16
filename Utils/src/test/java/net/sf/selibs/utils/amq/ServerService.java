package net.sf.selibs.utils.amq;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.utils.misc.UHelper;
import net.sf.selibs.utils.service.Service;
import org.apache.activemq.ActiveMQConnectionFactory;

@Slf4j
public class ServerService extends Service {

    //config=======================
    protected ActiveMQConnectionFactory cf;
    //work=========================
    protected Connection con;
    protected Session s;
    protected MessageConsumer consumer;
    protected Queue q;
    protected JMSMessageExchanger me;
    public CountingCL cl;

    public ServerService(ActiveMQConnectionFactory cf) {
        this.cf = cf;
    }

    @Override
    protected void doStuff() {
        try {
            EchoResponseProducer mp = new EchoResponseProducer();
            mp.setRequest(consumer.receive());
            me.makeResponse(mp);
        } catch (Exception ex) {
            log.warn("Cant process client message", ex);
        }
    }

    @Override
    protected void preAction() throws Exception {
        con = cf.createConnection();
        s = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        con.start();
        q = s.createQueue("jmsRPCqueue");
        consumer = s.createConsumer(q);
        me = new JMSMessageExchanger(cf, q);
        this.cl = new CountingCL();
        me.getConnectionListeners().add(cl);
    }

    @Override
    protected void postAction() {
        UHelper.close(consumer);
        UHelper.close(s);
        UHelper.close(con);
        UHelper.close(me);
    }

}
