package net.sf.selibs.tcp.nio.module;

import java.net.URI;
import java.net.URISyntaxException;
import junit.framework.Assert;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.CountingConnectionListener;
import net.sf.selibs.utils.misc.SyncCounter;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ClientModuleTest {

    ClientModule clients;
    TCPConfig cfg;
    HMessage request;
    HEchoClientProcessor processor;
    NModule server;

    public ClientModuleTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        //BasicConfigurator.configure();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        cfg = new TCPConfig("127.0.0.1", 2134);
        cfg.backlog = 100000;
        request = HMessage.createRequest(HMethods.POST, HVersions.V11, new URI("/"), "127.0.0.1:2134", "Hello".getBytes("UTF-8"));
        processor = new HEchoClientProcessor(request);
        this.clients = ClientModule.generateHttp(cfg, this.processor, request);
        this.server = EchoServer.generateHttpEcho(cfg);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void generalTest() throws Exception {
        System.out.println("========ClientModuleTest:generalTest==========");
        this.server.start();
        Thread.sleep(1000);
        //special sets
        this.clients.setClientCount(100);
        this.clients.setIterationCount(10);
        this.clients.setSleepMaxInterval(10);
        this.clients.setClientMessageQuantity(100);
        //
        this.clients.start();
        this.clients.startTest();
        this.clients.join();
        this.server.stop();
        this.server.join();

        System.out.println(this.clients.getSpeedCalc().getValue());
        System.out.println(this.clients.getClients().getConnectionListener());
        System.out.println(this.server.getConnectionListener());
        //checking connections
        int connectionCount = this.clients.getClientCount() * this.clients.getIterationCount();
        SyncCounter connectedClient = ((CountingConnectionListener) this.clients.getClients().getConnectionListener()).connected;
        Assert.assertEquals(connectionCount, connectedClient.get());
        SyncCounter disconnectedClient = ((CountingConnectionListener) this.clients.getClients().getConnectionListener()).disconnected;
        Assert.assertEquals(connectionCount, disconnectedClient.get());

        SyncCounter connectedServer = ((CountingConnectionListener) this.server.getConnectionListener()).connected;
        Assert.assertEquals(connectionCount, connectedServer.get());
        SyncCounter disconnectedServer = ((CountingConnectionListener) this.server.getConnectionListener()).connected;
        Assert.assertEquals(connectionCount, disconnectedServer.get());

    }

}
