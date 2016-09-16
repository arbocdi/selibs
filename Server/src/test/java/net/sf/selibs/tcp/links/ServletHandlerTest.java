package net.sf.selibs.tcp.links;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import junit.framework.Assert;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.ResponseLine;
import net.sf.selibs.http.constants.HCodes;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.http.constants.HNames;
import net.sf.selibs.http.constants.HValues;
import net.sf.selibs.http.constants.HVersions;
import net.sf.selibs.http.io.HSerializer;
import net.sf.selibs.http.servlet.EchoServlet;
import net.sf.selibs.http.servlet.HServlet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class ServletHandlerTest {

    ServletHandler servletState;
    TCPMessage ctx;

    HServlet servlet;

    public ServletHandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        servletState = new ServletHandler(null);
        ctx = new TCPMessage();
        ctx.out = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test400() throws Exception {
        System.out.println("==============ServletState:test400=================");
        byte[] wrongRequest = " cantparsethis\r\n\r\n".getBytes("UTF-8");
        ctx.in = new ByteArrayInputStream(wrongRequest);
        this.servletState.handle(ctx);
        HMessage response = HSerializer.fromStream(new ByteArrayInputStream(((ByteArrayOutputStream) ctx.out).toByteArray()));
        System.out.println(response);
        Assert.assertEquals(HCodes.WRONG_REQUEST, ((ResponseLine) response.line).code);

    }

    @Test
    public void test500() throws Exception {
        System.out.println("==============ServletState:test500=================");
        byte[] request = HSerializer.toByteArray(HMessage.createRequest(HMethods.GET, new URI("/hello"), "localhost:1010"));
        ctx.in = new ByteArrayInputStream(request);
        this.servletState.handle(ctx);
        HMessage response = HSerializer.fromStream(new ByteArrayInputStream(((ByteArrayOutputStream) ctx.out).toByteArray()));
        System.out.println(response);
        Assert.assertEquals(HCodes.INTERNAL_ERROR, ((ResponseLine) response.line).code);

    }

    @Test
    public void testOKConClose() throws Exception {
        System.out.println("==============ServletState:testOKConClose=================");
        this.servletState.setServlet(new OKServlet());
        byte[] request = HSerializer.toByteArray(HMessage.createRequest(HMethods.GET, new URI("/hello"), "localhost:1010"));
        ctx.in = new ByteArrayInputStream(request);
        this.servletState.handle(ctx);
        HMessage response = HSerializer.fromStream(new ByteArrayInputStream(((ByteArrayOutputStream) ctx.out).toByteArray()));
        System.out.println(response);
        Assert.assertEquals(HCodes.OK, ((ResponseLine) response.line).code);
        Assert.assertEquals(HValues.CLOSE, response.getHeaderValue(HNames.CONNECTION));
    }

    @Test
    public void testNot11() throws Exception {
        System.out.println("==========ServletState:testNot11=============");
        this.servletState.setServlet(new EchoServlet());
        HMessage hRequest = HMessage.createRequest(HMethods.GET,
                HVersions.V1,
                new URI("/hello"),
                "localhost:1010",
                null
        );
        System.out.println(hRequest);
        byte[] request = HSerializer.toByteArray(hRequest);
        ctx.in = new ByteArrayInputStream(request);
        this.servletState.handle(ctx);
        HMessage response = HSerializer.fromStream(new ByteArrayInputStream(((ByteArrayOutputStream) ctx.out).toByteArray()));
        System.out.println(response);
    }

    @Test
    public void testRequestConClose() throws Exception {
        System.out.println("==========ServletState:testRequestConClose=============");
        this.servletState.setServlet(new EchoServlet());
        HMessage hRequest = HMessage.createRequest(HMethods.GET,
                HVersions.V11,
                new URI("/hello"),
                "localhost:1010",
                null
        );
        hRequest.addHeader(HNames.CONNECTION, HValues.CLOSE);
        byte[] request = HSerializer.toByteArray(hRequest);
        ctx.in = new ByteArrayInputStream(request);
        this.servletState.handle(ctx);
        HMessage response = HSerializer.fromStream(new ByteArrayInputStream(((ByteArrayOutputStream) ctx.out).toByteArray()));
        System.out.println(response);
        Assert.assertTrue(response.getConnectionClose());
    }

}
