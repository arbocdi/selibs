package net.sf.selibs.velocity_ui.servlets;

import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.velocity_ui.jetty.ServerConfig;
import net.sf.selibs.velocity_ui.jetty.ServerConfig.ConnectorConfig;
import net.sf.selibs.velocity_ui.jetty.ServerConfig.ServletConfig;
import net.sf.selibs.velocity_ui.jetty.ServerConfig.ServletContextHandlerConfig;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class HNBServletTest {

    public HNBServletTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void testSomeMethod() throws Exception {
        System.out.println("========HNBServletTest:testSomeMethod=========");
        Injector i = new Injector();

        HNBConfig hnb = new HNBConfig();
        i.addBinding(HNBConfig.class, hnb);

        hnb.header = "Test";
        hnb.title = "Test";

        List<HLink> links = new LinkedList();
        links.add(new HLink("echo", "echo"));
        links.add(new HLink("cons", "connections"));
        links.add(new HLink("info", "info"));
        hnb.links = links;

        Dispatcher dis = Mockito.mock(Dispatcher.class);
        i.addBinding(Dispatcher.class, dis);
        List<Connection> cons = new LinkedList();
        Mockito.when(dis.getConnections()).thenReturn(cons);
        for (int idx = 0; idx < 100; idx++) {
            Connection con = Mockito.mock(Connection.class);
            Mockito.when(con.getLocalAddress()).thenReturn("127.0.0.1:8080");
            Mockito.when(con.getRemoteAddress()).thenReturn("127.0.0.1:3456");
            Mockito.when(con.getId()).thenReturn(idx);
            cons.add(con);
        }

        ServerConfig cfg = new ServerConfig();
        cfg.connectorConfig = new ConnectorConfig();
        cfg.connectorConfig.host = "127.0.0.1";
        cfg.connectorConfig.port = 2121;

        cfg.handlerConfig = new ServletContextHandlerConfig();
        cfg.handlerConfig.path = "/";
        cfg.handlerConfig.servlets = new LinkedList();

        ServletConfig echo = new ServletConfig();
        echo.path = "/echo";
        echo.servlet = new EchoServlet();
        cfg.handlerConfig.servlets.add(echo);

        ServletConfig consServlet = new ServletConfig();
        consServlet.path = "/cons";
        consServlet.servlet = new ConnectionsServlet();
        cfg.handlerConfig.servlets.add(consServlet);
        
        ServletConfig infoServlet = new ServletConfig();
        infoServlet.path = "/info";
        infoServlet.servlet = new InfoServlet("/info");
        cfg.handlerConfig.servlets.add(infoServlet);
        
        ServletConfig nfServlet = new ServletConfig();
        nfServlet.path = "/";
        nfServlet.servlet = new NotFoundServlet();
        cfg.handlerConfig.servlets.add(nfServlet);

        Server srv = cfg.createServer(i);
        srv.start();
        srv.join();
    }

}
