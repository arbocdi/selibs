package net.sf.selibs.utils.misc;

import java.io.IOException;
import java.net.Proxy;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.utils.amq.AMQCommon;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class WSClientTest {

    public WSClientTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        EmbeddedHttp.createSingleton();
        JettyLauncher jl = new JettyLauncher();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void makeRequest() throws IOException {
        System.out.println("==========WSClient:makeRequest===========");
        byte[] respData = WSClient.makeRequest("http://127.0.0.1:8000/test", "GET");
        String respStr = new String(respData, "UTF-8");
        System.out.println(respStr);
        Assert.assertTrue(respStr.contains("This is the response"));
    }

    @Test
    public void makePostRequest() throws IOException {
        System.out.println("==========WSClient:makePostRequest===========");
        String request = "Hello";
        byte[] respData = WSClient.makeRequest("http://127.0.0.1:7070/echo", "POST", request.getBytes("UTF-8"), "applictaion/text",Proxy.NO_PROXY);
        String respStr = new String(respData, "UTF-8");
        System.out.println(respStr);
        Assert.assertEquals(request, respStr);
    }

    @Test
    public void testLoad() throws IOException, InterruptedException {
        System.out.println("==========WSClient:testLoad===========");
        SyncCounter counter = new SyncCounter();
        
        List<Thread> threads = new LinkedList();
        for(int i=0;i<100;i++){
            threads.add(this.createClient(counter));
        }
        UHelper.startThreads(threads);
        while(counter.get()<100*100){
            System.out.println("Messages processed "+counter);
            Thread.sleep(1000);
        }
        UHelper.joinThreads(threads);
    }

    protected Thread createClient(final SyncCounter counter) {
        return new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        String request = "Hello";
                        byte[] respData = WSClient.makeRequest("http://127.0.0.1:7070/echo", "POST", request.getBytes("UTF-8"), "applictaion/text",Proxy.NO_PROXY);
                        String respStr = new String(respData, "UTF-8");
                        Assert.assertEquals(request, respStr);
                        counter.increment();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

}
