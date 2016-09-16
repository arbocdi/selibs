package net.sf.selibs.utils.io.streams;

import net.sf.selibs.utils.io.streams.CountingInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CountingInputStreamTest {

    public CountingInputStream cIn;
    public ByteArrayInputStream in;
    public byte[] data;

    public CountingInputStreamTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.data = new byte[]{1, 5, 2, 3, 4, 5, 10, 11, 23, 56, 76, 33};
        this.in = new ByteArrayInputStream(this.data);
        this.cIn = new CountingInputStream(2, in);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of read method, of class CountingInputStream.
     */
    @Test(expected = IOException.class)
    public void testRead() throws Exception {
        System.out.println("============testRead===============");
        Assert.assertEquals(1, this.cIn.read());
        Assert.assertEquals(5, this.cIn.read());
        Assert.assertEquals(2, this.cIn.bytesRead);
        this.cIn.read();
        

    }

    /**
     * Test of close method, of class CountingInputStream.
     */
    //ByteArrayInputSteam doesnt honor close method :)
    @Ignore
    @Test (expected=IOException.class)
    public void testClose() throws Exception {
        System.out.println("========testClose============");
        this.cIn.close();
        this.in.read();
        
    }

    /**
     * Test of reset method, of class CountingInputStream.
     */
    @Test
    public void testReset() throws Exception{
        System.out.println("==============testReset===============");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int read;
        while((read = this.cIn.read())>-1){
            bout.write(read);
            this.cIn.reset();
        }
        Assert.assertArrayEquals(this.data, bout.toByteArray());
    }
}