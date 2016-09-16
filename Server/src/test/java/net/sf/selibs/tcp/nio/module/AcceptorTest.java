package net.sf.selibs.tcp.nio.module;

import net.sf.selibs.tcp.nio.module.Dispatcher;
import net.sf.selibs.tcp.nio.module.Acceptor;
import net.sf.selibs.tcp.OpenClient;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.ConnectionListener;
import net.sf.selibs.tcp.nio.EchoProcessor;
import net.sf.selibs.utils.misc.SyncCounter;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author root
 */
public class AcceptorTest {

    SyncCounter igGen;
    SyncCounter messageCounter;
    boolean error;

    public AcceptorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        igGen = new SyncCounter();
        error = false;
        this.messageCounter = new SyncCounter();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void generalTest() throws Exception {
        int cltNum = 100;
        Acceptor acc = this.makeAcceptor();
        //acc.getDispatcher().launch();
        acc.start();
        Thread.sleep(1000);
        for (int cycle = 1; cycle <= 2; cycle++) {
            this.launchClients(cltNum);
            while (this.messageCounter.get()< cltNum * 1000*cycle) {
                Thread.sleep(500);
                System.out.println(" Messages processed " + this.messageCounter.get());
            }
            Thread.sleep(1000);
            Mockito.verify(acc.getDispatcher().getListener(), Mockito.times(cltNum*cycle)).connected(Mockito.any(Connection.class));
            Mockito.verify(acc.getDispatcher().getListener(), Mockito.times(cltNum*cycle)).disconnected(Mockito.any(Connection.class));
            Assert.assertTrue(acc.getDispatcher().connections.isEmpty());
        }
        acc.stop();
       // acc.getDispatcher().shutdown();

    }

    public Acceptor makeAcceptor() throws Exception {
        TCPConfig cfg = new TCPConfig();
        cfg.ip="127.0.0.1";
        cfg.port=4040;
        cfg.backlog=10000;
        Acceptor acc = new Acceptor(cfg);
        Dispatcher dis = new Dispatcher();
        acc.setDispatcher(dis);
        dis.setProcessor(new EchoProcessor());
        dis.setListener(Mockito.mock(ConnectionListener.class));
        return acc;
    }

    public void launchClients(int num) {
        for (int i = 0; i < num; i++) {
            Thread client = new Thread() {
                public void run() {
                    try {
                        launchClient();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            client.start();
        }
    }

    public void launchClient() throws Exception {
        OpenClient clt = null;
        try {
            TCPConfig cfg = new TCPConfig();
            cfg.ip = "127.0.0.1";
            cfg.port = 4040;
            cfg.timeout = 30000;
            clt = new OpenClient();
            this.igGen.increment();
            int id = this.igGen.get();
            clt.cfg = cfg;
            clt.openConnection();
            clt.openStreams();
            for (int i = 0; i < 1000; i++) {
                clt.getTo().write(id + i);
                Assert.assertEquals((byte) (id + i), (byte) clt.getFrom().read());
                messageCounter.increment();
            }
            clt.closeConnection();
        } catch (Exception ex) {
            clt.closeConnection();
            throw ex;
        }
    }
    @Test
    public void testLaucnhStop() throws Exception {
        TCPConfig cfg = new TCPConfig();
        cfg.ip="127.0.0.1";
        cfg.port=2216;
        cfg.backlog=10000;
        Acceptor acc = Mockito.spy(new Acceptor(cfg));
        acc.setDispatcher(new Dispatcher());
        acc.start();
        Thread.sleep(1000);
        Assert.assertTrue(acc.serverSocketChannel.socket().isBound());
        Assert.assertTrue(acc.dispatcher.selector.isOpen());
        acc.stop();
        Thread.sleep(1000);
        Assert.assertTrue(acc.serverSocketChannel.socket().isClosed());
        Assert.assertTrue(acc.dispatcher.isStopped());
    }

}
