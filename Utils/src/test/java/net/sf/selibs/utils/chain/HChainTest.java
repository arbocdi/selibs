package net.sf.selibs.utils.chain;

import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import lombok.ToString;
import net.sf.selibs.utils.graph.GraphUtils;
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
public class HChainTest {

    THandler handler;
    TLink link1;
    TLink link2;

    public HChainTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        handler = new THandler("handler");
        link1 = new TLink("link1");
        link2 = new TLink("link2");
        link1.setNext(link2);
        link2.setNext(handler);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getHandlers method, of class HandlerChain.
     */
    @Test
    public void testGetFilters() {
        System.out.println("==============HChain:testGetFilters===============");
        List<Handler> handlers = HChain.getHandlers(link1);
        System.out.println(handlers);
        Assert.assertEquals(3, handlers.size());
        Assert.assertEquals(link1, handlers.get(0));
        Assert.assertEquals(link2, handlers.get(1));
        Assert.assertEquals(handler, handlers.get(2));
    }

    @Test
    public void testGetFilters2() {
        System.out.println("==============HChain:testGetFilters2===============");
        List<Handler> handlers = HChain.getHandlers(link2);
        System.out.println(handlers);
        Assert.assertEquals(2, handlers.size());
        Assert.assertEquals(link2, handlers.get(0));
        Assert.assertEquals(handler, handlers.get(1));
    }

    @Test
    public void testInitChain() throws Exception {
        System.out.println("==============HChain:testInitChain===============");
        HChain.initChain(this.link1);
        List objects = new LinkedList();
        GraphUtils.graphToList(link1, objects);
        System.out.println(objects);
        Assert.assertTrue(link1.initCompleted);
        Assert.assertTrue(link2.initCompleted);
        Assert.assertTrue(handler.initCompleted);
    }

    @Test
    public void testAdd() {
        System.out.println("==============HChain:testAdd===============");
        HChain chain = new HChain();
        HLink link1 = new TestLink("link1");
        HLink link2 = new TestLink("link2");
        HLink link3 = new TestLink("link3");
        chain.add(link1);
        chain.add(link2);
        chain.add(link3);

        List<Handler> handlers = HChain.getHandlers(chain.getFirst());

        System.out.println(handlers);

        Assert.assertSame(handlers.get(0), link1);
        Assert.assertSame(handlers.get(1), link2);
        Assert.assertSame(handlers.get(2), link3);
    }

    @Test
    public void testGetHandler() {
        System.out.println("====================HChain:testGetHandler===================");
        Handler h = HChain.getHandler(TLink.class, link1);
        Assert.assertSame(link1, h);
        h = HChain.getHandler(Handler.class, link1);
        Assert.assertNull(h);
    }

    @ToString
    public static class TestLink extends HLink {

        protected String name;

        public TestLink(String name) {
            this.name = name;
        }

        @Override
        public Object handle(Object i) throws Exception {
            return null;
        }

    }

}
