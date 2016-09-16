package net.sf.selibs.messaging.sync.cb;

import java.io.File;
import net.sf.selibs.messaging.sync.cb.CBServer.CBSSerializer;
import net.sf.selibs.tcp.TCPConfig;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author root
 */
public class CBServerTest {

    CBServer server;

    public CBServerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        TCPConfig cfg = new TCPConfig();
        cfg.ip = "127.0.0.1";
        cfg.port = 2020;
        cfg.backlog = 1000;

        server = CBServer.generate(cfg, 1000);

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSaveLoad() throws Exception {
        System.out.println("===========CBServer:testSaveLoad=========");
        File xml = new File("cbServer");
        CBSSerializer s = new CBSSerializer(xml);
        s.save(server);

        CBServer srv = s.load();

        srv.start();
        Thread.sleep(1200);
        srv.stop();
        srv.join();
    }
}
