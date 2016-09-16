package net.sf.selibs.messaging.sync.cb;

import java.io.File;
import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.sync.cb.CBClient.CBCSerializer;
import net.sf.selibs.tcp.TCPConfig;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CBClientTest {

    CBClient clt;
    boolean error;

    public CBClientTest() {
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

        clt = CBClient.generate(new TCPConfig("127.0.0.1", 1121), 10 * 1000);
        error = false;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSaveLoad() throws Exception {
        System.out.println("============CBClient:testSaveLoad============");

        File xml = new File("cbClient");
        CBCSerializer s = new CBCSerializer(xml);
        s.save(clt);
        CBClient clt2 = s.load();

        clt2.start();
        clt2.stop();
        clt2.join();
    }

    @Test
    public void testClientServer() throws Exception {
        System.out.println("=============cb:testClientServer===========");
        final CBServer srv = CBServer.generate(new TCPConfig("127.0.0.1", 1214, 60000, 1000), 1000);
        final CBClient clt = CBClient.generate(new TCPConfig("127.0.0.1", 1214), 1000);
        srv.start();
        Thread.sleep(1000);
        clt.start();
        Thread.sleep(1000);

        //100 messages client<-server
        Thread srvClient = new Thread("srvClient") {
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        Message request = new Message();
                        request.destination = CBServer.ECHO;
                        request.source = "testCBServer";
                        Message response = srv.getME().exchange(request);
                        Assert.assertEquals(request.source, response.destination);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }
            }
        };
        srvClient.start();

        //100 messages client->server
        for (int i = 0; i < 100; i++) {
            Message request = new Message();
            request.destination = CBServer.ECHO;
            request.source = "testCBClient";
            Message response = clt.getSME().exchange(request);
            Assert.assertEquals(request.source, response.destination);
        }

        srvClient.join();
        srv.stop();
        srv.join();
        clt.stop();
        clt.join();
        Assert.assertFalse(error);
    }

}
