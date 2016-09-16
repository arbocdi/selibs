package net.sf.selibs.tcp;

import net.sf.selibs.tcp.factory.ThreadPoolFactory;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class ThreadPoolFactoryTest {
    
    ThreadPoolFactory factory;
    
    public ThreadPoolFactoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        factory = new ThreadPoolFactory();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSaveLoadProduce() throws Exception{
        System.out.println("=========ThreadPoolFactory:testSaveLoadProduce==========");
        Serializer persister = new Persister();
        File tpfXML = new File("tpf.xml");
        persister.write(this.factory, tpfXML);
        ThreadPoolFactory tpf = persister.read(ThreadPoolFactory.class, tpfXML);
        Assert.assertEquals(this.factory, tpf);
        System.out.println(tpf);
        ThreadPoolExecutor tpe = tpf.produce();
        tpe.shutdown();
        tpe.awaitTermination(10, TimeUnit.MINUTES);
        
    }
    
}
