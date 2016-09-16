
package net.sf.selibs.utils.service;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
public class ServiceTest {
    
    TService service;
    
    public ServiceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        service = new TService();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of start method, of class Service.
     */
    @Test
    public void testStartOK() throws Exception {
        System.out.println("=============Service:testStartOK===============");
        this.service.setSleepInterval(100);
        this.service.start();
        Thread.sleep(1000);
        this.service.stop();
        this.service.getWorker().join();
        System.out.println(this.service.actions);
        Assert.assertTrue(service.actions.contains("stuff"));
        Assert.assertTrue(service.actions.contains("post"));
    }
    @Test(expected = ServiceException.class)
    public void testStartError() throws Exception {
        System.out.println("=============Service:testStartError===============");
        this.service.setSleepInterval(100);
        this.service.start();
        this.service.start();
    }

    }

    
