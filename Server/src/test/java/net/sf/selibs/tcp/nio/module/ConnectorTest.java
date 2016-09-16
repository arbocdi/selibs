package net.sf.selibs.tcp.nio.module;

import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import net.sf.selibs.tcp.nio.module.Connector;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.TCPServer;
import net.sf.selibs.tcp.factory.ThreadPoolFactory;
import net.sf.selibs.tcp.links.BinaryEcho;
import net.sf.selibs.tcp.links.ConnectionCounterLink;
import net.sf.selibs.tcp.links.StreamsLink;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.ConnectionListener;
import net.sf.selibs.tcp.nio.CountingConnectionListener;
import net.sf.selibs.tcp.nio.Processor;
import net.sf.selibs.utils.chain.HChain;
import net.sf.selibs.utils.misc.SyncCounter;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ConnectorTest {

    SyncCounter msgCount;

    public ConnectorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        //BasicConfigurator.configure();
        //Logger.getRootLogger().setLevel(Level.ALL);

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        msgCount = new SyncCounter();
    }

    @After
    public void tearDown() {
    }

    @Test(timeout = 60000)
    /**
     * Server:binary echo - echoes all bytes sent. Client:adds connection
     * request end sends client number 1000 times;
     */
    public void generalTest() throws Exception {
        System.out.println("========generalTest========");
        TCPServer srv = this.createServer();
        srv.start();
        final Connector con = this.createConnector();
        //con.getDispatcher().launch();
        con.start();
        Thread.sleep(1000);
        for (int cycle = 1; cycle <= 5; cycle++) {
            int cltNum = 100;
            for (int clt = 0; clt < cltNum; clt++) {
                final int cltNumber = clt;
                Thread t = new Thread() {
                    public void run() {
                        try {
                            Connection conn = new Connection();
                            conn.getAttachments().put("counter", new SyncCounter());
                            conn.getAttachments().put("cltNumber", cltNumber);
                            con.addConnectRequest(conn);
                            byte[] data = {(byte) cltNumber};
                            for (int i = 0; i < 1000; i++) {
                                con.getDispatcher().addWriteRequest(conn, data);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                t.start();
            }
            while (this.msgCount.get() < 1000 * cltNum * cycle) {
                System.out.println("Messages processed " + this.msgCount.get());
                Thread.sleep(500);
            }
            
            Assert.assertEquals(1000 * cltNum * cycle, msgCount.get());
            
            int connectedCount = ((CountingConnectionListener) con.getDispatcher().getListener()).connected.get();
            int disconnectedCount = ((CountingConnectionListener) con.getDispatcher().getListener()).disconnected.get();

            while (connectedCount < cltNum * cycle || disconnectedCount < cltNum * cycle) {
                System.out.println(" Connection established: "+connectedCount);
                System.out.println(" Connection closed: "+disconnectedCount);
                
            }
            
            Assert.assertEquals(cltNum * cycle, connectedCount);
            Assert.assertEquals(cltNum * cycle, disconnectedCount);
            
            Assert.assertTrue(con.getDispatcher().connections.isEmpty());
        }

        srv.stop();
        con.stop();
        //con.getDispatcher().shutdown();

    }

    public TCPServer createServer() {
        TCPConfig cfg = new TCPConfig();
        cfg.ip = "127.0.0.1";
        cfg.timeout = 30000;
        cfg.port = 2121;
        cfg.backlog = 10000;

        StreamsLink streams = new StreamsLink();
        streams.setNext(new BinaryEcho());

        TCPServer srv = new TCPServer(cfg, new ThreadPoolFactory(), streams);
        srv.getTpf().maxThreads = 1000;
        return srv;
    }

    public Connector createConnector() {
        TCPConfig cfg = new TCPConfig("127.0.0.1", 2121);
        Dispatcher disp = new Dispatcher();
        disp.setListener(new CountingConnectionListener());
        disp.setProcessor(new TestProcessor());
        Connector con = new Connector(cfg);
        con.setDispatcher(disp);
        return con;

    }

    class TestProcessor implements Processor {

        @Override
        public void process(byte[] data, Connection con, Dispatcher dis) {
            try {
                Assert.assertNotNull(dis.getConnection(con.getId()));
                for (byte bt : data) {
                    Assert.assertEquals(((Integer) con.getAttachments().get("cltNumber")).byteValue(), bt);
                }
                msgCount.increment(data.length);
                SyncCounter c2 = (SyncCounter) con.getAttachments().get("counter");
                c2.increment(data.length);
                if (c2.get() >= 1000) {
                    dis.close(con);
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

    }

    @Test
    public void testLaunchShutdown() throws Exception {
        System.out.println("======testLaucnhShutdown========");
        TCPConfig cfg = new TCPConfig("127.0.0.1", 5121);
        Connector con = new Connector(cfg);
        Dispatcher dis = new Dispatcher();
        dis.processor = Mockito.mock(Processor.class);
        dis.listener = Mockito.mock(ConnectionListener.class);
        con.setDispatcher(dis);
        con.start();
        Thread.sleep(1000);
        Assert.assertTrue(con.selector.isOpen());
        Assert.assertTrue(con.dispatcher.selector.isOpen());
        con.stop();
        con.join();
        Assert.assertTrue(con.isStopped());
        Assert.assertTrue(con.dispatcher.isStopped());

    }

    @Test(timeout = 30000)
    /**
     * Create test server and connector. Dispatcher closes any connections,
     * registered with it. Connection requests added in multiple threads. Count
     * connections and disconnections on server, verify them.
     */

    public void testAddConnectionRequest() throws Exception {
        System.out.println("============ConnectorTest:testAddConnectionRequest=============");
        //creating tcp server
        TCPConfig cfg = new TCPConfig();
        cfg.ip = "127.0.0.1";
        cfg.timeout = 30000;
        cfg.port = 2122;
        cfg.backlog = 10000;

        HChain chain = new HChain();
        chain.add(new StreamsLink());
        ConnectionCounterLink counterLink = new ConnectionCounterLink();
        chain.add(counterLink);
        chain.add(new BinaryEcho());

        TCPServer srv = new TCPServer(cfg, new ThreadPoolFactory(), chain.getFirst());
        srv.getTpf().maxThreads = 2000;

        //create connector with mock Dispatcher
        Dispatcher closingDispatcher = Mockito.mock(Dispatcher.class);
        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Connection con = (Connection) invocation.getArguments()[0];
                con.getChannel().close();
                return null;
            }
        }).when(closingDispatcher).register(Mockito.any(Connection.class));
        final Connector con = new Connector(cfg);
        con.setDispatcher(closingDispatcher);
        //test parameters
        final int threadCount = 10;
        final int connCount = 100;
        List<Thread> threads = new LinkedList();
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < connCount; i++) {
                            con.addConnectRequest(new Connection());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            threads.add(t);
        }
        try {
            srv.start();
            con.start();
            Thread.sleep(1000);
            for (Thread t : threads) {
                t.start();
            }
            for (Thread t : threads) {
                t.join();
            }
            while (counterLink.getConnected().get() < threadCount * connCount) {
                Thread.sleep(100);
            }
            while (counterLink.getDisconnected().get() < threadCount * connCount) {
                Thread.sleep(100);
            }
            Assert.assertEquals(threadCount * connCount, counterLink.getConnected().get());
            Assert.assertEquals(threadCount * connCount, counterLink.getDisconnected().get());

        } finally {
            srv.stop();
            srv.join();
            con.stop();
            con.join();
        }

    }

    /**
     * Verifying that connections are closed if its not possible to connect.
     *
     * @throws Exception
     */
    @Test(timeout = 30000)
    public void testAddConnectionRequestError() throws Exception {
        System.out.println("============ConnectorTest:testAddConnectionRequestError=============");
        TCPConfig cfg = new TCPConfig();
        cfg.ip = "127.0.0.1";
        cfg.port = 2128;
        Connector con = new Connector(cfg);
        con.setDispatcher(Mockito.mock(Dispatcher.class));
        con.start();
        for (int i = 0; i < 1000; i++) {
            Connection conection = new Connection();
            try {
                con.addConnectRequest(conection);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        int keySetSize = 10;
        while (keySetSize != 0) {
            synchronized (con.guard) {
                con.selector.wakeup();
                System.out.println(con.selector.keys().size());
                keySetSize = con.selector.keys().size();
            }
            Thread.sleep(100);
        }
        con.stop();
        con.join();

    }

}
