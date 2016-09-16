package net.sf.selibs.tcp.nio.http;

import java.io.ByteArrayInputStream;
import java.net.URI;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.tcp.nio.Connection;
import net.sf.selibs.tcp.nio.module.Dispatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class HEchoProcessorTest {

    HEchoProcessor processor;
    Dispatcher dis;

    public HEchoProcessorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.processor = new HEchoProcessor();
        dis = Mockito.mock(Dispatcher.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of process method, of class HEchoProcessor.
     */
    @Test
    public void testProcess() throws Exception {
        System.out.println("process");
        final Connection con = new Connection();
        HMessage req = HMessage.createRequest(HMethods.GET,new URI("/res.txt"), "127.0.0.1:8080");
        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Assert.assertEquals(con, args[0]);
                ByteArrayInputStream bin = new ByteArrayInputStream((byte[]) args[1]);
                HMessage response = HSerializer.fromStream(bin);
                System.out.println(response);
                Assert.assertTrue(response.toString().contains("/res.txt"));
                return null;
            }
        }).when(dis).addWriteRequest(Mockito.any(Connection.class), Mockito.any(byte[].class));
        this.processor.process(con, req, dis);
    }

}
