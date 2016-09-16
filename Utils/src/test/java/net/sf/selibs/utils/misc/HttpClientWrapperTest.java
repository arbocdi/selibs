package net.sf.selibs.utils.misc;

import java.util.LinkedList;
import java.util.List;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author root
 */
public class HttpClientWrapperTest {

    public HttpClientWrapperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
    public void testMakeOneStringRequest() throws Exception {
        System.out.println("============HttpClientWrapperTest:testMakeOneStringRequest===============");
        HttpClientWrapper hClientWrapper = new HttpClientWrapper();
        try {
            hClientWrapper.start();

            HttpPost post = new HttpPost("http://127.0.0.1:7070/echo");

            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .build();
            post.setConfig(defaultRequestConfig);

            StringEntity postData = new StringEntity("Привет!",
                    ContentType.create("text/plain", "UTF-8"));
            post.setEntity(postData);
            String response = hClientWrapper.makeStringRequest(post);
            Assert.assertEquals("Привет!", response);

        } finally {
            hClientWrapper.stop();
            hClientWrapper.join();
        }
    }

    @Test
    public void testMakeStringRequest() throws Exception {
        System.out.println("============HttpClientWrapperTest:testMakeStringRequest===============");
        HttpClientWrapper hClientWrapper = new HttpClientWrapper();
        try {
            hClientWrapper.start();

            SyncCounter counter = new SyncCounter();

            List<Thread> clients = new LinkedList();
            for (int i = 0; i < 1; i++) {
                clients.add(this.createClient(hClientWrapper, counter));
            }
            UHelper.startThreads(clients);
            while (counter.get() < 100 * 100) {
                System.out.println("Messages processd " + counter.get());
                Thread.sleep(1000);
            }
            UHelper.joinThreads(clients);

        } finally {
            hClientWrapper.stop();
            hClientWrapper.join();
        }
    }

    public Thread createClient(final HttpClientWrapper hClientWrapper, final SyncCounter counter) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < 10000; i++) {
                        HttpPost post = new HttpPost("http://127.0.0.1:7070/echo");

                        StringEntity postData = new StringEntity("Привет!",
                                ContentType.create("text/plain", "UTF-8"));
                        post.setEntity(postData);
                        String response = hClientWrapper.makeStringRequest(post);
                        Assert.assertEquals("Привет!", response);
                        counter.increment();
                        //Thread.sleep(2000);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        return thread;
    }
}
