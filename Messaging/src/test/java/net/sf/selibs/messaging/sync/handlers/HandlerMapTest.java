
package net.sf.selibs.messaging.sync.handlers;

import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.MessageStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ashevelev
 */
public class HandlerMapTest {
    
    MessageHandler echo;
    HandlerMap hm;
    
    public HandlerMapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        echo = new EchoHandler();
        hm = new HandlerMap();
        hm.addHandler(echo, "echo");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of handle method, of class HandlerMap.
     */
    @Test
    public void testHandleOK() throws Exception{
        System.out.println("==========testHandleOK============");
        Message request = new Message();
        request.destination="echo";
        Message responce = this.hm.exchange(request);
        System.out.println(responce);
        Assert.assertEquals(request.destination, responce.source);
        Assert.assertEquals(MessageStatus.OK, responce.status);
    }
    @Test(expected = NullPointerException.class)
    public void testHandleERR() throws Exception{
        System.out.println("==========testHandleERR============");
        this.hm.removeHandler("echo");
        Message request = new Message();
        request.destination="echo";
        Message responce = this.hm.exchange(request);
    }

    /**
     * Test of addHandler method, of class HandlerMap.
     */
    @Test
    public void testAddHandler() {
    }

    /**
     * Test of removeHandler method, of class HandlerMap.
     */
    @Test
    public void testRemoveHandler() {
    }

    /**
     * Test of exchange method, of class HandlerMap.
     */
    @Test
    public void testExchange() throws Exception {
    }

    /**
     * Test of init method, of class HandlerMap.
     */
    @Test
    public void testInit() {
    }

    /**
     * Test of setReady method, of class HandlerMap.
     */
    @Test
    public void testSetReady() {
    }
    
}
