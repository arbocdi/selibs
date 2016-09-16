package net.sf.selibs.utils.io.streams;

import net.sf.selibs.utils.io.streams.CountingOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class CountingOutputStreamTest {

    ByteArrayOutputStream out;
    CountingOutputStream counter;

    public CountingOutputStreamTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.out = Mockito.spy(new ByteArrayOutputStream());
        this.counter = new CountingOutputStream(this.out);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of write method, of class CountingOutputStream.
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("==========testWrite=============");
        String message = "Hello Serialization";
        byte[] msgByte = message.getBytes("UTF-8");
        this.counter.write(msgByte);
        Assert.assertArrayEquals(this.out.toByteArray(), msgByte);
        Assert.assertEquals(msgByte.length, this.counter.getBytesWritten());
    }

    /**
     * Test of close method, of class CountingOutputStream.
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("=========testClose==========");
        this.counter.close();
        Mockito.verify(out).close();
    }

    /**
     * Test of flush method, of class CountingOutputStream.
     */
    @Test
    public void testFlush() throws Exception {
        System.out.println("===========testFlush==============");
        this.counter.flush();
        Mockito.verify(this.out).flush();
    }

    /**
     * Test of reset method, of class CountingOutputStream.
     */
    @Test
    public void testReset() throws IOException {
        System.out.println("=======testReset==========");
        this.counter.write(12);
        this.counter.reset();
        Assert.assertEquals(0, this.counter.bytesWritten);
    }

    /**
     * Test of getBytesWritten method, of class CountingOutputStream.
     */
    @Test
    public void testGetBytesWritten() {
    }


}
