package net.sf.selibs.messaging.sync.handlers;

import net.sf.selibs.messaging.Message;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author ashevelev
 */
public class EchoLinkTest {
    
    EchoLink echo;
    MessageHandler next;
    
    public EchoLinkTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        echo = new EchoLink();
        next = Mockito.mock(MessageHandler.class);
        echo.setNext(next);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testEchangeOK() throws Exception {
        System.out.println("===========testEchangeOK===============");
        Message request = new Message();
        request.payload = "hello";
        request.destination = this.echo.getEcho();
        Message response = echo.exchange(request);
        Assert.assertEquals(request.payload, response.payload);
    }
    @Test
    public void testEchangeNULL() throws Exception {
        System.out.println("===========testEchangeNULL===============");
        Message request = new Message();
        request.payload = "hello";
        request.destination = "echo1";
        Message response = echo.exchange(request);
        Assert.assertNull(response);
    }
    
}
