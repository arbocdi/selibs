package net.sf.selibs.messaging.sync.handlers;

import net.sf.selibs.messaging.Message;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author ashevelev
 */
public class MHandlerChainTest {
    
    MHandlerChain chain;
    
    public MHandlerChainTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        chain = new MHandlerChain() {

            @Override
            public Message exchange(Message request) throws Exception {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        chain.next = Mockito.mock(HandlerChain.class);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setReady method, of class MHandlerChain.
     */
    @Test
    public void testSetReady() {
        System.out.println("=========testSetReady==========");
        this.chain.setReady(true);
        Mockito.verify(chain.next).setReady(Mockito.eq(true));
        this.chain.setReady(false);
        Mockito.verify(chain.next).setReady(Mockito.eq(false));
       
    }

    
    
}
