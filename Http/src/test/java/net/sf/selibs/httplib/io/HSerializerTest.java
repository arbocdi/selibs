package net.sf.selibs.httplib.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Arrays;
import net.sf.selibs.http.io.HSerializer;
import junit.framework.Assert;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.constants.HTTPSamples;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.nio.ParserException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
public class HSerializerTest {

    public HSerializerTest() {
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

    /**
     * Test of toStream method, of class HSerializer.
     */
    @Test
    public void testToStream() throws Exception {
    }

    /**
     * Test of toByteArray method, of class HSerializer.
     */
    @Test
    public void testToByteArray() throws Exception {
    }

    /**
     * Test of fromString method, of class HSerializer.
     */
    @Test
    public void testFromToString() throws Exception {
        System.out.println("============HSerializer:testFromString================");
        HMessage okResponse = HSerializer.fromString(HTTPSamples.OK_HEADER);
        System.out.println(okResponse);
        Assert.assertEquals(HTTPSamples.OK_HEADER, okResponse.toString());
    }

    @Test
    public void testFromToStringChunked() throws Exception {
        System.out.println("============HSerializer:testFromStringChunked================");
        HMessage response = HSerializer.fromString("HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/xml; charset=UTF-8\r\n"
                + "Transfer-Encoding: chunked");
        System.out.println(response);
    }

    @Test
    public void testFromToStringMultiline() throws Exception {
        System.out.println("============HSerializer:testFromToStringMultiline================");
        HMessage okResponse = HSerializer.fromString(HTTPSamples.OK_MULTILINE_HEADER);
        System.out.println(okResponse);
        //Assert.assertEquals(HTTPSamples.OK_HEADER, okResponse.toString());
    }

    @Test
    public void testFromStream() throws Exception {
        System.out.println("============HSerializer:testFromStream================");
        HMessage get = HMessage.createRequest(HMethods.GET, new URI("/test.xml"), "127.0.0.1:8080");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        HSerializer.toStream(bout, get);
        HMessage get2 = HSerializer.fromStream(new ByteArrayInputStream(bout.toByteArray()));
        System.out.println(get2);
        Assert.assertEquals(get, get2);
    }

    @Test(expected = ParserException.class)
    public void testFromStreamErrorMissingBytes() throws Exception {
        System.out.println("============HSerializer:testFromStreamErrorMissingBytes================");
        try {
            HMessage get = HMessage.createRequest(HMethods.POST, HVersions.V11, new URI("/test.xml"), "127.0.0.1:8080", "Hello arbocdi".getBytes("UTF-8"));
            System.out.println(get);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            HSerializer.toStream(bout, get);
            byte[] data = bout.toByteArray();
            HMessage get2 = HSerializer.fromStream(new ByteArrayInputStream(Arrays.copyOf(data, data.length - 1)));
        } catch (Exception ex) {
            Assert.assertFalse(ex.getMessage().contains("arbocdi"));
            ex.printStackTrace();
            throw ex;
        }
    }

}
