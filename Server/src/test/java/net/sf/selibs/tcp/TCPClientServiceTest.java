package net.sf.selibs.tcp;

import net.sf.selibs.tcp.links.TCPMessage;
import org.apache.log4j.BasicConfigurator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author root
 */
public class TCPClientServiceTest {
    
    TCPClientService clientService;
    TCPClient client;
    
    public TCPClientServiceTest() {
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
        TCPConfig cfg = new TCPConfig();
        cfg.ip="127.0.0.1";
        cfg.port=12345;
        
        client = Mockito.mock(TCPClient.class);
        Mockito.when(client.getCfg()).thenReturn(cfg);
        Mockito.doThrow(new Exception("Test Error")).when(client).handle();
        
        this.clientService = new TCPClientService(100, client);
        
    }
    @Test(timeout=60000)
    public void testDoStuff() throws Exception{
        System.out.println("==========TCPClientService:testDoStuff===========");
        this.clientService.start();
        Thread.sleep(1000);
        this.clientService.stop();
        this.clientService.getWorker().join();
        Mockito.verify(client, Mockito.atLeast(5)).handle();
    }
    
}
