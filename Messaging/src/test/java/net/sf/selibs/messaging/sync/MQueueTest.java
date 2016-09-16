package net.sf.selibs.messaging.sync;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ashevelev
 */
public class MQueueTest {

    MQueue queue;

    public MQueueTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.queue = new MQueue();
        error = false;
    }

    @After
    public void tearDown() {
    }

    boolean error;

    /**
     * Test of enqueue method, of class MQueue.
     */
    @Test(timeout = 60000)
    public void testEnqueueDeque() throws Exception {
        System.out.println("=======testEnqueueDeque=========");
        Thread enq = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        queue.enqueue(1);
                        Thread.sleep(10);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }

            }
        };
        enq.start();
        Thread enq2 = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        queue.enqueue(2);
                        Thread.sleep(12);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }

            }
        };
        enq2.start();
        Thread deq = new Thread() {
            public void run() {
                try {
                    int sum=0;
                    for (int i = 0; i < 200; i++) {
                        int ii = (Integer) queue.dequeue();
                        sum+=ii;
                        System.out.println(ii);
                    }
                    Assert.assertEquals(300, sum);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }

            }
        };
        deq.start();
        enq.join();
        enq2.join();
        deq.join();
        Assert.assertFalse(error);
    }

    /**
     * Test of dequeue method, of class MQueue.
     */
    @Test(timeout = 60000)
    public void testDequeueClose() throws Exception {
        System.out.println("==========testDequeueClose==========");
        Thread deq = new Thread() {
            public void run() {
                try {
                    int ii = (Integer) queue.dequeue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }

            }
        };
        deq.start();
        Thread.sleep(1000);
        queue.close();
        deq.join();
        Assert.assertTrue(error);
    }
    @Test(timeout = 60000)
    public void testEnqueueClose() throws Exception {
        System.out.println("==========testEnqueueClose==========");
        Thread deq = new Thread() {
            public void run() {
                try {
                    queue.enqueue("Hello");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    error = true;
                }

            }
        };
        deq.start();
        Thread.sleep(1000);
        queue.close();
        deq.join();
        Assert.assertTrue(error);
    }

    /**
     * Test of setClosed method, of class MQueue.
     */
    @Test
    public void testSetClosed() {
        
    }
    @Test(timeout=10000)
    public void testInterruptedDequeue() throws Exception {
        System.out.println("==========testInterruptedDequeue=============");
        Thread sender = new Thread(){
            public void run(){
                try{
                queue.dequeue();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        };
        sender.start();
        sender.interrupt();
        sender.join();
    }
     @Test(timeout=10000)
    public void testInterruptedEnqueue() throws Exception {
        System.out.println("==========testInterruptedEnqueue=============");
        Thread sender = new Thread(){
            public void run(){
                try{
                queue.enqueue("Hello");
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        };
        sender.start();
        sender.interrupt();
        sender.join();
    }

}
