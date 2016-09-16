package net.sf.selibs.http.nio;

import net.sf.selibs.http.nio.ByteStack;
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
public class ByteStackTest {

    ByteStack bs;

    public ByteStackTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        bs = new ByteStack(2);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addByte method, of class ByteStack.
     */
    @org.junit.Test
    public void testAddByte() {
        System.out.println("=========ByteStack:testAddByte==========");
        bs.addByte((byte) 10);
        bs.addByte((byte) 11);
        Assert.assertArrayEquals(new byte[]{10, 11}, bs.stack);
        bs.addByte((byte) 12);
        Assert.assertArrayEquals(new byte[]{11, 12}, bs.stack);
        bs.addByte((byte) 20);
        Assert.assertArrayEquals(new byte[]{12, 20}, bs.stack);
    }

}
