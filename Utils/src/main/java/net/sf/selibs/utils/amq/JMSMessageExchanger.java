package net.sf.selibs.utils.amq;

import java.util.LinkedList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.misc.UHelper;

public class JMSMessageExchanger {

    @Setter
    @Getter
    protected long timeout = 60 * 1000;
    @Getter
    protected List<ConnectionListener> connectionListeners = new LinkedList();

    public JMSMessageExchanger(ConnectionFactory cf) {
        this.cf = cf;
    }

    public JMSMessageExchanger(ConnectionFactory cf, Queue queue) {
        this.cf = cf;
        this.queue = queue;
    }
    //work
    protected ConnectionFactory cf;
    protected Queue queue;
    protected TemporaryQueue tempQueue;
    protected Connection connection;
    protected Session session;
    protected MessageProducer producer;
    protected MessageConsumer consumer;
    //status
    protected boolean started = false;
    protected int mid = 0;

    public Message makeRequest(RequestProducer producer) throws Exception {
        try {
            if (!this.started) {
                this.init();
                this.tempQueue = this.session.createTemporaryQueue();
                this.consumer = this.session.createConsumer(tempQueue);
            }
            //send request
            Message requestM = producer.produce(this.session);
            mid++;
            requestM.setJMSCorrelationID(String.valueOf(mid));
            requestM.setJMSReplyTo(this.tempQueue);
            this.producer.send(this.queue, requestM);
            //get response
            while (true) {
                Message responseM = this.consumer.receive(this.timeout);
                if (responseM == null) {
                    return null;
                }
                int midResp = Integer.parseInt(responseM.getJMSCorrelationID());
                if (mid == midResp) {
                    return responseM;
                } else {
                    //just get other message
                }
            }

        } catch (Exception ex) {
            this.close();
            throw ex;
        }
    }

    public void makeResponse(ResponseProducer producer) throws Exception {
        try {
            if (!this.started) {
                this.init();
            }
            Message response = producer.produce(this.session);
            response.setJMSCorrelationID(producer.getRequest().getJMSCorrelationID());
            this.producer.send(producer.getRequest().getJMSReplyTo(), response);

        } catch (Exception ex) {
            this.close();
            throw ex;
        }
    }

    protected void init() throws Exception {
        this.connection = cf.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.producer = this.session.createProducer(null);
        this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        this.connection.start();
        this.started = true;
        this.notifyConnectionAquired(connection);
    }

    public void close() {
        UHelper.close(producer);
        UHelper.close(consumer);
        UHelper.close(session);
        UHelper.close(connection);
        if (this.started) {
            this.notifyConnectionReleased(connection);
        }
        this.started = false;
    }

    protected void notifyConnectionAquired(Connection con) {
        for (ConnectionListener listener : this.connectionListeners) {
            listener.connectionAquired(con);
        }
    }

    protected void notifyConnectionReleased(Connection con) {
        for (ConnectionListener listener : this.connectionListeners) {
            listener.connectionReleased(con);
        }
    }

}
