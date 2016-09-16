package net.sf.selibs.messaging.sync.tcp;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.messaging.Message;
import net.sf.selibs.messaging.MessageExchanger;
import net.sf.selibs.messaging.MessageExchangerName;
import net.sf.selibs.messaging.sync.SME;
import net.sf.selibs.messaging.sync.handlers.EchoHandler;
import net.sf.selibs.messaging.sync.handlers.MessageHandler;
import net.sf.selibs.tcp.links.ObjectLink;
import net.sf.selibs.tcp.links.TCPMessage;
import net.sf.selibs.utils.inject.Injector;
import net.sf.selibs.utils.locator.ServiceLocator;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class ClientSideTest {

    PipedInputStream cltIn;
    PipedInputStream srvIn;
    PipedOutputStream cltOut;
    PipedOutputStream srvOut;

    Initiator initiator;
    ObjectLink initHandler;
    Responder responder;
    ObjectLink respHandler;

    TCPMessage ctxSrv;
    TCPMessage ctxClt;

    MessageHandler handler;
    boolean error = false;
    SME sme;

    Thread server;
    Thread client;

    Injector injector;

    public ClientSideTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {

        sme = new SME();

        injector = new Injector();

        error = false;

        cltIn = new PipedInputStream();
        srvIn = new PipedInputStream();
        cltOut = new PipedOutputStream(srvIn);
        srvOut = new PipedOutputStream(cltIn);

        ctxSrv = new TCPMessage();
        ctxSrv.out = srvOut;
        ctxSrv.in = srvIn;
        initiator = new Initiator();
        this.initHandler = new ObjectLink();
        this.initHandler.setNext(initiator);

        ctxClt = new TCPMessage();
        ctxClt.out = cltOut;
        ctxClt.in = cltIn;
        this.respHandler = new ObjectLink();

        handler = Mockito.spy(new EchoHandler());

        responder = new Responder();
        this.respHandler.setNext(responder);

        this.client = this.createClient();
        this.server = this.createServer();

        injector.addBinding(SME.class, sme);
        injector.addBinding(MessageHandler.class, handler);
        injector.injectInto(this.initiator);
        injector.injectInto(this.responder);

        server.start();
        client.start();
        Thread.sleep(1000);

    }

    @After
    public void tearDown() throws InterruptedException {
        Thread.sleep(1000);
        client.interrupt();
        server.interrupt();
        client.join();
        server.join();
    }

    protected Thread createServer() {
        Thread serverThread = new Thread() {
            public void run() {
                try {
                    initHandler.handle(ctxSrv);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }
            }
        };
        return serverThread;
    }

    protected Thread createClient() {
        Thread clientThread = new Thread() {
            @Override
            public void run() {
                try {
                    respHandler.handle(ctxClt);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }
            }
        };
        return clientThread;
    }

    @Test
    public void testProcessOK() throws Exception {
        System.out.println("==============testProcessOK================");
        Thread.sleep(2000);
        List<Thread> threads = new LinkedList();
        for (int t = 0; t < 20; t++) {
            Thread client = new Thread() {
                public void run() {
                    try {
                        for (int i = 0; i < 10; i++) {
                            Message msg = new Message();
                            msg.payload = this.getName() + "#" + i;
                            Message responce = sme.exchange(msg);
                            System.out.println(responce);
                            Assert.assertEquals(msg.payload, responce.payload);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        error = true;
                    }
                }
            };
            client.start();
            threads.add(client);
            Assert.assertFalse(error);
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("==========================================");
    }

    @Test(expected = Exception.class)
    public void testProcessErrorSend() throws Exception {
        System.out.println("==============testProcessErrorSend================");
        Thread.sleep(1000);
        this.srvOut.close();
        try {
            Message request = new Message();
            Message responce = sme.exchange(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            System.out.println("==========================================");
        }

    }

    @Test
    public void testProcessHandleException() throws Exception {
        System.out.println("==============testProcessHandleException================");
        Thread.sleep(1000);
        this.srvOut.close();
        Mockito.verify(this.handler).setReady(true);
        Mockito.verify(this.handler).setReady(false);
        System.out.println("==========================================");

    }

    @Test(expected = Exception.class)
    public void testProcessErrorRecieve() throws Exception {
        System.out.println("==============testProcessErrorRecieve================");
        Thread.sleep(1000);
        this.cltIn.close();
        try {
            Message request = new Message();
            Message responce = sme.exchange(request);
            System.out.println(responce);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            System.out.println("==========================================");
        }
    }

    @Test(expected = Exception.class)
    public void testProcessErrorHandler() throws Exception {
        System.out.println("==============testProcessErrorHandler================");
        try {
            this.responder.mh = new ErrorHandler();
            Message request = new Message();
            Message responce = sme.exchange(request);
            System.out.println(responce);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            System.out.println("==========================================");
        }
    }

    /**
     * Test of makeExchangeCycle method, of class ClientSide.
     */
    @Test
    public void testMakeExchangeCycle() throws Exception {
    }

    /**
     * Test of clone method, of class ClientSide.
     */
    @Test
    public void testClone() {
    }

    /**
     * Test of toString method, of class ClientSide.
     */
    @Test
    public void testToString() {
    }

    @Test(timeout = 10000)
    public void testClientInterrupted() throws Exception {
        System.out.println("============testClientInterrupted==============");
        this.responder.mh = new SleepHandler();
        Thread userT = new Thread() {
            public void run() {
                Message request = new Message();
                try {
                    sme.exchange(request);
                    error = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        userT.start();
        Thread.sleep(500);
        userT.interrupt();
        userT.join();
        Thread.sleep(1000);
        Thread userT2 = new Thread() {
            public void run() {
                Message request = new Message();
                try {
                    sme.exchange(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }
            }
        };
        userT2.start();
        userT2.join();
        Assert.assertFalse(error);
        System.out.println("==========================================");
    }

    public static class ErrorHandler implements MessageHandler {

        @Override
        public Message exchange(Message request) throws Exception {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void init() {
        }

        @Override
        public void setReady(boolean ready) {
        }

    }

    public static class SleepHandler implements MessageHandler {

        @Override
        public Message exchange(Message request) throws Exception {
            Thread.sleep(1000);
            return request.createOKResponce();
        }

        @Override
        public void init() {
        }

        @Override
        public void setReady(boolean ready) {
        }

    }

}
