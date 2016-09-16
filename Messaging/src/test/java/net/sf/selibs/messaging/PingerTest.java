package net.sf.selibs.messaging;

import net.sf.selibs.messaging.sync.SME;
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
public class PingerTest {
    
    Pinger pinger;
    SME me;
    
    public PingerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        me= new SME();
        pinger = new Pinger(100);
        pinger.setMe(me);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of doRun method, of class Pinger.
     */
    @Test
    public void testDoRun() throws Exception {
        System.out.println("=========testDoRun==========");
        me.setReady(true);
        pinger.start();
        for(int i=0;i<10;i++){
            Message request=me.getRequest();
            Assert.assertEquals(pinger.getDestination(), request.destination);
            Assert.assertEquals(pinger.getSource(), request.source);
            me.setResponse(request.createOKResponce());
        }
        pinger.stop();
    }

    
    
}
