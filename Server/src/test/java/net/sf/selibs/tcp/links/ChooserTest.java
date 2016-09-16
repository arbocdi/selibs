package net.sf.selibs.tcp.links;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import net.sf.selibs.utils.chain.HChain;
import net.sf.selibs.utils.chain.HLink;
import net.sf.selibs.utils.chain.Handler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class ChooserTest {
    
    ChooserHandler srv;
    Handler<TCPMessage,Void> link;
    
    ChooserClient clt;
    HLink<TCPMessage,Void> linkClt;
    
    TCPMessage msg;
    
    ByteArrayOutputStream bout;
    ByteArrayInputStream bin;
    
    final String NAME = "name";
    
    
    public ChooserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
        msg = new TCPMessage();
        
        bout = new ByteArrayOutputStream();
        msg.out = bout;
        
        srv = new ChooserHandler();
        link = Mockito.mock(HLink.class);
        srv.addHandler(NAME, link);
        
        clt = new ChooserClient(NAME);
        linkClt  = Mockito.mock(HLink.class);
        clt.setNext(linkClt);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testServerClient() throws Exception {
        System.out.println("===========CtxChooserTest:testServerClient==========");
        clt.handle(msg);
        Mockito.verify(this.linkClt).handle(msg);
        msg.in = new ByteArrayInputStream(this.bout.toByteArray());
        srv.handle(msg);
        Mockito.verify(this.link).handle(msg);

    }
    @Test
    @Ignore
    public void testInit() throws Exception {
        System.out.println("===========CtxChooserTest:testInit==========");
        HChain.initChain(this.clt);
        
        
    }
    
}
