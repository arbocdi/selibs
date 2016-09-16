package net.sf.selibs.utils.io;

import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IOUtilsTest {

    public IOUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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
    public void testReadFully() throws Exception {
        System.out.println("=========IOUtilsTest:testReadFully==========");
        String data = "Hello";
        ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
        String dataRead = new String(IOUtils.readFully(in), "UTF-8");
        System.out.println(dataRead);
        Assert.assertEquals(data, dataRead);
    }
    @Test
    public void testReadFullyReader() throws Exception {
        System.out.println("=========IOUtilsTest:testReadFullyReader==========");
        String data = "Привет!";
        Reader rd = new StringReader(data);
        String result = IOUtils.readFully(rd);
        Assert.assertEquals(data, result);
    }

}
