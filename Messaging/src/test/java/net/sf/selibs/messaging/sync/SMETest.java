package net.sf.selibs.messaging.sync;

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
public class SMETest {

    SME sme;

    public SMETest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.sme = new SME();
        error = false;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of exchange method, of class SME.
     */
    protected boolean error;

    @Test
    public void testExchange() throws Exception {
        System.out.println("==========testExchange=============");
        //проверю, что SME действительно может менять состояния готов\не готов
        this.sme.setReady(false);
        try {
            this.sme.getRequest();
        } catch (Exception ex) {

        }
        sme.setReady(true);
        Thread clt1 = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        Message request = new Message();
                        request.status = MessageStatus.OK;
                        request.payload = "T1" + i;
                        Message responce = sme.exchange(request);
                        Assert.assertEquals(request.payload, responce.payload);
                    }
                } catch (Exception ex) {
                    System.out.println("T1");
                    ex.printStackTrace();
                    error = true;
                }
            }
        };
        clt1.start();
        Thread clt2 = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        Message request = new Message();
                        request.status = MessageStatus.OK;
                        request.payload = "T2" + i;
                        Message responce = sme.exchange(request);
                        Assert.assertEquals(request.payload, responce.payload);
                    }
                } catch (Exception ex) {
                    System.out.println("T2");
                    ex.printStackTrace();
                    error = true;
                }
            }
        };
        clt2.start();
        Thread echo = new Thread() {
            public void run() {
                try {
                    while (true) {
                        sme.setResponse(sme.getRequest());
                    }
                } catch (Exception ex) {
                }
            }
        };
        echo.start();
        clt1.join();
        clt2.join();
        Assert.assertFalse(error);
    }

    @Test(expected = ClosedException.class)
    public void testSetReady() throws Exception {
        System.out.println("=========testSetReady============");
        this.sme.setReady(false);
        Message request = new Message();
        this.sme.exchange(request);
    }

    @Test(expected = ClosedException.class)
    public void testSetReady2() throws Exception {
        System.out.println("=========testSetReady2============");
        this.sme.setReady(false);
        Message request = new Message();
        this.sme.setResponse(request);
    }

    @Test(expected = ClosedException.class)
    public void testSetReady3() throws Exception {
        System.out.println("=========testSetReady3============");
        this.sme.setReady(false);
        this.sme.getRequest();
    }

    @Test(expected = Exception.class)
    public void testExchangeError() throws Exception {
        System.out.println("=========testExchangeError=============");
        sme.setReady(true);
        Thread responder = new Thread() {
            public void run() {
                try {
                    Message request = sme.getRequest();
                    sme.setResponse(request.createErrorResponce(new Exception("Test error")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        responder.start();
        Message request = new Message();
        this.sme.exchange(request);
    }

    @Test(expected = ClosedException.class)
    public void testInterrupt() throws Exception {
        System.out.println("=========testInterrupt===========");
        this.sme.setReady(true);
        Thread userT = new Thread() {
            public void run() {
                try {
                    sme.exchange(new Message());
                    error = true;
                } catch (Exception ex) {

                }
            }
        };
        userT.start();
        Thread.sleep(500);
        userT.interrupt();
        userT.join();
        Assert.assertFalse(error);
        sme.exchange(new Message());
    }

}
