package net.sf.selibs.messaging.sync.handlers;

import java.util.List;
import junit.framework.Assert;
import lombok.ToString;
import net.sf.selibs.messaging.Message;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class HandlerChainTest {

    HandlerChainImpl chain;
    MessageHandler next;

    public HandlerChainTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        chain = new HandlerChainImpl();
        next = Mockito.mock(MessageHandler.class);
    }

    @After
    public void tearDown() {
    }

   
    @Test
    public void testGetChainLinks() throws Exception {
        System.out.println("========testGetChainLinks==========");
        HandlerChain nextChain = new HandlerChainImpl();
        nextChain.setNext(next);
        this.chain.setNext(nextChain);
        List<MessageHandler> links = this.chain.getChainLinks();
        System.out.println(links);
        Assert.assertEquals(3, links.size());
        
    }
    @ToString
    public class HandlerChainImpl extends HandlerChain {

        @Override
        public void setReady(boolean ready) {
        }

        
        @Override
        public Message exchange(Message request) throws Exception {
            return request;
        }
    }

    /**
     * Test of init method, of class HandlerChain.
     */
    @Test
    public void testInit() {
        System.out.println("=========testInit==========");
        this.chain.setNext(next);
        this.chain.init();
        Mockito.verify(this.next).init();
    }

    

}
