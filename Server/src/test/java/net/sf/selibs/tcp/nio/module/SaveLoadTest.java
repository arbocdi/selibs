package net.sf.selibs.tcp.nio.module;

import java.io.File;
import net.sf.selibs.tcp.TCPConfig;
import net.sf.selibs.tcp.nio.http.HEchoProcessor;
import net.sf.selibs.tcp.nio.http.HttpBridge;
import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class SaveLoadTest {

    @BeforeClass
    public static void before() {
        BasicConfigurator.configure();
    }

    @Test
    public void testServer() throws Exception {
        System.out.println("===========SaveLoadTest:testServer=============");
        TCPConfig cfg = new TCPConfig();
        cfg.backlog = 1000;
        cfg.ip = "127.0.0.1";
        cfg.port = 1256;

        NModule httpServer = NModule.generateServer(cfg);

        HttpBridge processor = new HttpBridge();
        processor.httpProc = new HEchoProcessor();
        httpServer.setProcessor(processor);

        File xml = new File("nioServer.xml");
        Serializer persister = new Persister();
        persister.write(httpServer, xml);
        NModule httpServer2 = persister.read(NModule.class, xml);
        httpServer2.start();
        Thread.sleep(1200);
        httpServer2.stop();
        httpServer2.join();

    }

    @Test
    public void testClient() throws Exception {
        System.out.println("===========SaveLoadTest:testClient=============");
        TCPConfig cfg = new TCPConfig();
        cfg.backlog = 1000;
        cfg.ip = "127.0.0.1";
        cfg.port = 1256;

        NModule httpServer = NModule.generateClient(cfg);

        HttpBridge processor = new HttpBridge();
        processor.httpProc = new HEchoProcessor();
        httpServer.setProcessor(processor);

        File xml = new File("nioClient.xml");
        Serializer persister = new Persister();
        persister.write(httpServer, xml);
        NModule httpServer2 = persister.read(NModule.class, xml);
        httpServer2.start();
        Thread.sleep(1200);
        httpServer2.stop();
        httpServer2.join();
    }

    @Test
    public void testHClient() throws Exception {
        System.out.println("===========SaveLoadTest:testHClient=============");

        NModule httpServer = NModule.generateClient();

        HttpBridge processor = new HttpBridge();
        processor.httpProc = new HEchoProcessor();
        httpServer.setProcessor(processor);

        File xml = new File("nioHClient.xml");
        Serializer persister = new Persister();
        persister.write(httpServer, xml);
        NModule httpServer2 = persister.read(NModule.class, xml);
        httpServer2.start();
        Thread.sleep(1200);
        httpServer2.stop();
        httpServer2.join();
    }
}
