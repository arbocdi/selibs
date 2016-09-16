package net.sf.selibs.tcp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import net.sf.selibs.tcp.factory.ThreadPoolFactory;
import net.sf.selibs.tcp.links.BinaryEcho;
import net.sf.selibs.tcp.links.BufferedLink;
import net.sf.selibs.tcp.links.StreamsLink;
import net.sf.selibs.tcp.links.TCPMessage;
import net.sf.selibs.utils.chain.HLink;
import net.sf.selibs.utils.inject.Injector;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author root
 */
public class TCPServerTest {

    public TCPServerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ALL);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        error = false;
    }

    @After
    public void tearDown() {
    }

    boolean error;

    @Test(timeout = 60000)
    public void testServerClient() throws Exception {
        System.out.println("============TCPServer:testServerClient===============");
        makeServer("echoServer.xml");
        makeClient("echoClient.xml");

        Serializer persister = new Persister();

        TCPServer srv = persister.read(TCPServer.class, new File("echoServer.xml"));
        srv.init();
        srv.inject(new Injector());
        srv.start();

        List<Thread> threads = new LinkedList();
        for (int i = 0; i < 100; i++) {
            final TCPClient clt = persister.read(TCPClient.class, new File("echoClient.xml"));
            Thread cThread = new Thread() {
                @Override
                public void run() {
                    try {
                        clt.handle();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        error = true;
                    }
                    finally{
                        clt.close();
                    }
                }
            };
            cThread.start();
            threads.add(cThread);
        }
        for (Thread thread : threads) {
            thread.join();
        }
        srv.stop();
        srv.getWorker().join();
        srv.getTpe().awaitTermination(100, TimeUnit.SECONDS);

        Assert.assertFalse(error);

    }

    public static void makeServer(String file) throws Exception {
        TCPConfig srvCfg = new TCPConfig();
        srvCfg.ip = "127.0.0.1";
        srvCfg.port = 12345;
        srvCfg.backlog = 1000;

        HLink<TCPMessage, Void> streams = new StreamsLink();
        HLink bufferedLink = (HLink) streams.setNext(new BufferedLink());
        bufferedLink.setNext(new BinaryEcho());

        ThreadPoolFactory tpf = new ThreadPoolFactory();
        tpf.maxThreads = 1000;

        TCPServer srv = new TCPServer(srvCfg, tpf, streams);
        Serializer persister = new Persister();
        File srvXML = new File(file);
        persister.write(srv, srvXML);
    }

    public static void makeClient(String file) throws Exception {
        TCPConfig cltCfg = new TCPConfig();
        cltCfg.ip = "127.0.0.1";
        cltCfg.port = 12345;
        cltCfg.timeout = 10000;

        HLink<TCPMessage, Void> streams = new StreamsLink();
        HLink bufferedLink = (HLink) streams.setNext(new BufferedLink());
        bufferedLink.setNext(new BinaryEchoClient());

        TCPClient clt = new TCPClient(cltCfg, streams);
        Serializer persister = new Persister();
        
        File clientXML = new File(file);
        persister.write(clt, clientXML);
    }

}
