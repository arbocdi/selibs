package net.sf.selibs.utils.amq;

import junit.framework.Assert;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class ExchangeTest {

    @Test(timeout = 120000)
    public void test() throws Exception {
        System.out.println("===========AMQExchangeTest:test============");
        AMQCommon.createSingletonBroker();
        Logger.getRootLogger().setLevel(Level.INFO);
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(AMQCommon.brokerURI);
        ServerService server = new ServerService(cf);
        server.start();
        Thread.sleep(1000);
        ClientsService clients = new ClientsService(cf, 100, 100);
        clients.start();
        clients.join();
        server.stop();
        server.join();
        Assert.assertEquals(100 * 100, clients.counter.get());
        for (CountingCL cl : clients.cls) {
            Assert.assertEquals(1, cl.aquireCounter.get());
            Assert.assertEquals(1, cl.releaseCounter.get());
        }
        Assert.assertEquals(1, server.cl.aquireCounter.get());
        Assert.assertEquals(1, server.cl.releaseCounter.get());

    }

}
