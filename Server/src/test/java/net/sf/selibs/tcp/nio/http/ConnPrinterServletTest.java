package net.sf.selibs.tcp.nio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import net.sf.selibs.tcp.nio.DispatcherName;
import net.sf.selibs.utils.locator.ServiceLocator;
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
public class ConnPrinterServletTest {

    ConPrinterServlet servlet;
    SocketChannel channel;
    InetSocketAddress remote;
    InetSocketAddress local;

    public ConnPrinterServletTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        this.servlet = new ConPrinterServlet();
        channel = Mockito.mock(SocketChannel.class);
        this.local = new InetSocketAddress("127.0.0.1", 1010);
        this.remote = new InetSocketAddress("109.71.71.1", 2138);
        Mockito.when(this.channel.getLocalAddress()).thenReturn(this.local);
        Mockito.when(this.channel.getRemoteAddress()).thenReturn(this.remote);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of respond method, of class ConnPrinterServlet.
     */
    @Test
    public void testRespond() throws Exception {
        System.out.println("=============respond=============");
        Connection con = new Connection();
        con.setChannel(channel);
        List<Connection> cons = new LinkedList();
        cons.add(con);
        cons.add(con);
        Dispatcher dis = Mockito.mock(Dispatcher.class);
        Mockito.when(dis.getConnections()).thenReturn(cons);
        //ServiceLocator.put(servlet.dispatcherName,dis);
        this.servlet.setDispatcher(dis);
        HMessage response = this.servlet.handle(null);
        String html = new String(response.payload, "UTF-8");
        System.out.println(response.toString());
        Assert.assertTrue(html.contains(String.valueOf(con.getId())));
        Assert.assertTrue(html.contains(con.getLocalAddress()));
        Assert.assertTrue(html.contains(con.getRemoteAddress()));
    }

    /**
     * Test of convert method, of class ConnPrinterServlet.
     */
    @Test
    public void testConvert_Collection() {
        System.out.println("==========convertCollection=========");
        Connection con = new Connection();
        con.setChannel(channel);
        List<Connection> cons = new LinkedList();
        cons.add(con);
        cons.add(con);
        List<ConEntity> ents = this.servlet.convert(cons);
        Assert.assertEquals(2, ents.size());
        for (ConEntity ent : ents) {
            Assert.assertEquals(this.servlet.convert(con), ent);
        }
    }

    /**
     * Test of convert method, of class ConnPrinterServlet.
     */
    @Test
    public void testConvert_Connection() {
        System.out.println("===========convertOne===========");
        Connection con = new Connection();
        con.setChannel(channel);
        ConEntity ent = this.servlet.convert(con);
        System.out.println(ent);
        Assert.assertEquals(con.getId(), ent.id);
        Assert.assertEquals(con.getLocalAddress(), ent.local);
        Assert.assertEquals(con.getRemoteAddress(), ent.remote);

    }

}
