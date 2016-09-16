/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.http;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Arrays;
import junit.framework.Assert;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.utils.io.IOUtils;
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
public class HMessageTest {

    public HMessageTest() {
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
    public void testGetEncoding() throws Exception {
        System.out.println("===========HMessageTest:testGetEncoding==============");
        String payload = "Привет!";
        HMessage request = HMessage.createRequest(HMethods.POST,
                HVersions.V11, new URI("/"),
                "127.0.0.1", payload.getBytes("UTF-16"));
        //encoding not specified
        String encoding = request.getEncoding();
        System.out.println(String.format("Default encoding %s", encoding));
        Assert.assertEquals("UTF-8", encoding);
        //encoding specified correctly
        request.addHeader(HNames.CONTENT_TYPE, "text/thml; charset=utf-16");
        encoding = request.getEncoding();
        System.out.println(String.format("Specified encoding %s", encoding));
        Assert.assertEquals("UTF-16", encoding);
        //encoding specified incorrectly
        request.addHeader(HNames.CONTENT_TYPE, "text/thml; ");
        encoding = request.getEncoding();
        System.out.println(String.format("Default encoding %s", encoding));
        Assert.assertEquals("UTF-8", encoding);

    }

    @Test
    public void testGetReaderRegular() throws Exception {
        System.out.println("===========HMessageTest:testGetReaderRegular==============");
        String payload = "Привет!";
        HMessage request = HMessage.createRequest(HMethods.POST,
                HVersions.V11, new URI("/"),
                "127.0.0.1", payload.getBytes("UTF-16"));
        request.addHeader(HNames.CONTENT_TYPE, "text/html; charset=utf-16");
        String result = IOUtils.readFully(request.getReader());
        Assert.assertEquals(payload, result);
    }

    @Test
    public void testGetReaderChunked() throws Exception {
        System.out.println("===========HMessageTest:testGetReaderChunked==============");
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
       for(byte b:"aaa".getBytes("UTF-16")){
           System.out.print(((int)b)&0x00FF);
           System.out.print(" ");
       }
        System.out.println("");
        payload.write("8\r\n".getBytes("UTF-8"));
        payload.write("abc".getBytes("UTF-16"));
        payload.write("\r\n".getBytes("UTF-8"));
        payload.write("0\r\n\r\n".getBytes("UTF-8"));
        payload.write("will not be read".getBytes("UTF-8"));
        
        HMessage request = HMessage.createRequest(HMethods.POST,
                HVersions.V11, new URI("/"),
                "127.0.0.1", payload.toByteArray());
        request.addHeader(HNames.TRANSFER_ENCODING, HValues.CHUNKED);
        request.addHeader(HNames.CONTENT_TYPE, "text/html; charset=utf-16");
        String result = IOUtils.readFully(request.getReader());
        System.out.println(result);
        Assert.assertEquals("abc", result);
    }

}
