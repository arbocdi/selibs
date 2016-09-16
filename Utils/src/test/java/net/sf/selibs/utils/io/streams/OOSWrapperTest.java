package net.sf.selibs.utils.io.streams;

import net.sf.selibs.utils.io.streams.OOSWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class OOSWrapperTest {
    
    ByteArrayOutputStream out;
    OOSWrapper wrapper;
    
    public OOSWrapperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws IOException {
        out = new ByteArrayOutputStream();
        wrapper = new OOSWrapper(out);
        wrapper.objectOut = Mockito.spy(wrapper.objectOut);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of writeObject method, of class OOSWrapper.
     */
    @Test
    public void testWriteObjectNoReset() throws Exception {
        System.out.println("========testWriteObjectNoReset========");
        String msg = "Hello";
        this.wrapper.writeObject(msg);
        Mockito.verify(this.wrapper.objectOut, Mockito.times(0)).reset();
        Mockito.verify(this.wrapper.objectOut).flush();
        ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(this.out.toByteArray()));
        String object = (String) oin.readObject();
        Assert.assertEquals(msg, object);
    }
     @Test
    public void testWriteObjectReset() throws Exception {
        System.out.println("========testWriteObjectReset========");
        String msg = "Hello";
        this.wrapper.limit=0;
        this.wrapper.writeObject(msg);
        Mockito.verify(this.wrapper.objectOut).reset();
        Mockito.verify(this.wrapper.objectOut).flush();
        ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(this.out.toByteArray()));
        String object = (String) oin.readObject();
        Assert.assertEquals(msg, object);
    }

    /**
     * Test of write method, of class OOSWrapper.
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("===========testWrite============");
        this.wrapper.write(10);
        this.wrapper.flush();
        ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(this.out.toByteArray()));
        Assert.assertEquals(10, oin.read());
    }

    /**
     * Test of writeObject method, of class OOSWrapper.
     */
    @Test
    public void testWriteObject() throws Exception {
    }

    /**
     * Test of flush method, of class OOSWrapper.
     */
    @Test
    public void testFlush() throws Exception {
    }

    /**
     * Test of close method, of class OOSWrapper.
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("=========testClose============");
        this.wrapper.close();
        Mockito.verify(wrapper.objectOut).close();
    }

    /**
     * Test of getLimit method, of class OOSWrapper.
     */
    @Test
    public void testGetLimit() {
    }

    /**
     * Test of setLimit method, of class OOSWrapper.
     */
    @Test
    public void testSetLimit() {
    }
    
}
