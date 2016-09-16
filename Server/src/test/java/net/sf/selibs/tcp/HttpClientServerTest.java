package net.sf.selibs.tcp;

import java.io.File;
import java.net.URI;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.http.servlet.EchoServlet;
import net.sf.selibs.http.servlet.HServlet;
import net.sf.selibs.http.servlet.HServletMap;
import net.sf.selibs.tcp.factory.ThreadPoolFactory;
import net.sf.selibs.tcp.links.BufferedLink;
import net.sf.selibs.tcp.links.ServletHandler;
import net.sf.selibs.tcp.links.StreamsLink;
import net.sf.selibs.tcp.links.StubHandler;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class HttpClientServerTest {

    @BeforeClass
    public static void init() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ALL);
    }

    @Test
    public void test() throws Exception {
        System.out.println("============TestHttpServerClient==============");
        File httpServer = new File("httpServer.xml");
        File httpClient = new File("httpClient.xml");

        this.makeServer(httpServer);
        this.makeClient(httpClient);

        Serializer persister = new Persister();
        TCPServer srv = persister.read(TCPServer.class, httpServer);
        TCPClient clt = persister.read(TCPClient.class, httpClient);
        srv.start();
        Thread.sleep(1000);
        clt.handle();
        for (int i = 0; i < 10; i++) {
            String host = "testhost:whatever";
            HMessage getEcho = HMessage.createRequest(HMethods.GET, new URI("/echo"), host);
            HSerializer.toStream(clt.getMsg().out, getEcho);
            HMessage response = HSerializer.fromStream(clt.getMsg().in);
            System.out.println(response);
            Assert.assertTrue(response.toString().contains(host));
        }
        clt.close();
        srv.stop();
        srv.getWorker().join();

    }

    public void makeClient(File file) throws Exception {
        TCPConfig cfg = new TCPConfig();
        cfg.ip = "127.0.0.1";
        cfg.port = 1657;

        StreamsLink streams = new StreamsLink();
        BufferedLink buffered = new BufferedLink();
        streams.setNext(buffered);
        StubHandler stub = new StubHandler();
        buffered.setNext(stub);

        TCPClient client = new TCPClient(cfg, streams);
        Serializer persister = new Persister();
        persister.write(client, file);
    }

    public void makeServer(File file) throws Exception {
        TCPConfig cfg = new TCPConfig();
        cfg.backlog = 1000;
        cfg.ip = "127.0.0.1";
        cfg.port = 1657;

        StreamsLink streams = new StreamsLink();
        BufferedLink buffered = new BufferedLink();
        streams.setNext(buffered);

        HServletMap sMap = new HServletMap();
        HServlet echo = new EchoServlet();
        sMap.addServlet("/echo", echo);
        ServletHandler servlets = new ServletHandler(sMap);
        buffered.setNext(servlets);

        TCPServer server = new TCPServer(cfg, new ThreadPoolFactory(), streams);
        Serializer persister = new Persister();
        persister.write(server, file);
    }
}
