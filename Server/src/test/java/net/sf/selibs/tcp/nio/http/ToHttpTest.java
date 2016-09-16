package net.sf.selibs.tcp.nio.http;

import java.net.URI;
import java.util.Arrays;

import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

/**
 *
 * @author root
 */
public class ToHttpTest {

    HttpBridge processor;
    HttpProcessor hProcessor;
    Dispatcher dis;

    public ToHttpTest() {
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
        this.processor = new HttpBridge();
        this.hProcessor = Mockito.mock(HttpProcessor.class);
        this.processor.httpProc = this.hProcessor;
        this.dis = Mockito.mock(Dispatcher.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of process method, of class HttpBridge.
     */
    @Test
    public void testProcess() throws Exception {
        System.out.println("process");
        Connection con = new Connection();
        HMessage req1 = HMessage.createRequest(HMethods.GET, new URI("test.xml"), "127.0.0.1:2020");
        byte[] raw = HSerializer.toByteArray(req1);
        byte[] part1 = Arrays.copyOfRange(raw, 0, 10);
        byte[] part2 = Arrays.copyOfRange(raw, 10, raw.length);
        this.processor.process(part1, con, dis);
        this.processor.process(part2, con, dis);
        this.processor.process(raw, con, dis);
        Mockito.verify(this.hProcessor, Mockito.times(2)).process(Mockito.eq(con), Mockito.eq(req1), Mockito.eq(dis));
    }

    @Test
    public void testProcessError() throws Exception {
        System.out.println("=========testProcessError=========");
        HMessage req1 = HMessage.createRequest(HMethods.GET, new URI("test.xml"), "127.0.0.1:2020");
        Connection con = new Connection();
        byte[] raw = HSerializer.toByteArray(req1);
        byte[] raw2 = new byte[raw.length + 1];
        System.arraycopy(raw, 0, raw2, 0, raw.length);
        this.processor.process(raw2, con, dis);
        Mockito.verify(this.dis).close(con);
    }

}
