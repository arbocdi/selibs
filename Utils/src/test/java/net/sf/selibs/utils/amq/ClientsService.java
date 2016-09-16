package net.sf.selibs.utils.amq;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.utils.misc.SyncCounter;
import net.sf.selibs.utils.misc.UHelper;
import net.sf.selibs.utils.service.Service;
import org.apache.activemq.ActiveMQConnectionFactory;

@Slf4j
public class ClientsService extends Service {

    //work==============
    public List<CountingCL> cls = Collections.synchronizedList(new LinkedList());
    protected SyncCounter counter;
    protected Connection con;
    protected Session s;
    Queue q;
    //config===================
    protected int clients;
    protected int messages;
    protected ActiveMQConnectionFactory cf;

    public ClientsService(ActiveMQConnectionFactory cf, int clients, int messages) {
        this.cf = cf;
        this.clients = clients;
        this.messages = messages;
        counter = new SyncCounter();
    }

    @Override
    protected void preAction() throws Exception {
        con = cf.createConnection();
        s = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        q = s.createQueue("jmsRPCqueue");
    }

    @Override
    protected void postAction() {
        UHelper.close(con);
        UHelper.close(s);
    }

    @Override
    protected void doStuff() {
        try {
            this.startClients(cf, q, clients, messages);
            this.stop();
        } catch (Exception ex) {
            log.warn("Cant execute clients", ex);
        }
    }

    public void startClients(final ConnectionFactory cf, final Queue queue, int clientQuantity, int messageQuantity) throws Exception {
        List<Thread> clients = new LinkedList();
        for (int clientNum = 0; clientNum < clientQuantity; clientNum++) {
            Thread cThread = makeClient(cf, queue, messageQuantity, clientNum);
            clients.add(cThread);
            cThread.start();

        }
        while (counter.get() < clientQuantity * messageQuantity) {
            System.out.println(String.format("Messages processed %s", counter.get()));
            Thread.sleep(1000);
        }
        for (Thread client : clients) {
            client.join();
        }
    }

    public Thread makeClient(final ConnectionFactory cf, final Queue queue, final int messageQuantity, final int clientNum) {
        Thread client = new Thread() {
            @Override
            public void run() {
                JMSMessageExchanger me = new JMSMessageExchanger(cf, queue);
                CountingCL cl = new CountingCL();
                cls.add(cl);
                me.getConnectionListeners().add(cl);
                try {
                    for (int messageNum = 0; messageNum < messageQuantity; messageNum++) {
                        String request = String.format("Sender %s, message %s", clientNum, messageNum);
                        EchoRequestProducer ep = new EchoRequestProducer();
                        ep.propValue = request;
                        Message response = me.makeRequest(ep);
                        if (!request.equals(response.getStringProperty(EchoRequestProducer.PROP_NAME))) {
                            System.out.println("===================================");
                            System.out.println(request);
                            System.out.println(response);
                            System.out.println("===================================");
                            throw new Exception("Response doesnt match request!");
                        } else {
                            counter.increment();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    me.close();
                }
            }

        };
        return client;
    }

}
