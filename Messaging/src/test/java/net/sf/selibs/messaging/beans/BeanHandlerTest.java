package net.sf.selibs.messaging.beans;

import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.sync.SME;
import net.sf.selibs.messaging.sync.handlers.EchoHandler;
import net.sf.selibs.messaging.sync.handlers.HandlerMap;
import net.sf.selibs.messaging.sync.handlers.LocalConnector;
import net.sf.selibs.messaging.sync.handlers.SnifferLink;
import net.sf.selibs.messaging.sync.handlers.SnifferLink.StdoutDestination;
import net.sf.selibs.utils.service.ServiceException;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Slf4j
@Ignore
public class BeanHandlerTest {

    BeanME clientStub;
    SME sme;
    LocalConnector localConnector;
    SnifferLink sniffer;
    HandlerMap hm;
    BeanHandler beanHandler;
    Bean1 b1;
    Bean2 b2;

    public BeanHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws ServiceException {
        clientStub = new BeanME();
        sme = new SME();
        this.clientStub.me = sme;
        localConnector = new LocalConnector();

        sniffer = new SnifferLink();
        sniffer.setDest(new StdoutDestination());

        hm = new HandlerMap();
        beanHandler = new BeanHandler();
        hm.addHandler(beanHandler, clientStub.dest);
        hm.addHandler(new EchoHandler(), "echo");

        sniffer.setNext(hm);

        b1 = new Bean1();
        b2 = new Bean2();
        beanHandler.getBeans().put("b1", b1);
        beanHandler.getBeans().put("b2", b2);

        localConnector.me = sme;
        localConnector.mh = sniffer;

        error = false;

        this.localConnector.start();
    }

    @After
    public void tearDown() throws InterruptedException {

        this.localConnector.stop();
        this.localConnector.join();
    }

    @Test
    public void testEcho() throws Exception {
        System.out.println("=========BeanHandlerTest:testEcho============");
        Thread.sleep(1000);
        for (int i = 0; i < 100; i++) {
            Message request = new Message();
            request.destination = "echo";
            request.payload = i;
            Message response = this.sme.exchange(request);
            Assert.assertEquals(response.payload, request.payload);
        }
    }

    boolean error;

    @Test
    public void testInvokeBean1() throws Exception {
        System.out.println("=========BeanHandlerTest:testInvokeBean1============");
        Thread.sleep(1000);

        List<Thread> threads = new LinkedList();
        for (int t = 0; t < 100; t++) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        Class[] classes = {int.class, int.class};
                        for (int i = 0; i < 100; i++) {
                            Object[] params = {10, i};
                            int result = (Integer) clientStub.exchange("b1", "add", classes, params);
                            Assert.assertEquals(10 + i, result);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        error = true;
                    }
                }
            };
            threads.add(thread);
            thread.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        Assert.assertFalse(error);
    }

    @Test
    public void testInvokeBean2() throws Exception {
        System.out.println("=========BeanHandlerTest:testInvokeBean2============");
        Thread.sleep(1000);
        Class[] classes = {String.class};
        for (int i = 0; i < 100; i++) {
            Object[] params = {String.valueOf(i)};
            Object result = this.clientStub.exchange("b2", "doAction", classes, params);
            Assert.assertNull(result);
            Assert.assertTrue(b2.actions.contains(String.valueOf(i)));
        }

    }

}
