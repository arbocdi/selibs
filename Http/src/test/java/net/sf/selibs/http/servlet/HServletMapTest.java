package net.sf.selibs.http.servlet;

import java.net.URI;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.ResponseLine;
import net.sf.selibs.http.constants.HCodes;
import net.sf.selibs.http.constants.HMethods;
import net.sf.selibs.utils.chain.HException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class HServletMapTest {

    HServletMap servlet;

    public HServletMapTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.servlet = new HServletMap();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addServlet method, of class MServletMapper.
     */
    @Test
    public void testAddServlet() {
        System.out.println("=================testAddServlet====================");
        HServlet s1 = Mockito.mock(HServlet.class);
        this.servlet.addServlet("/path", s1);
        Assert.assertEquals(s1, this.servlet.getServlet("/path"));
    }

    /**
     * Test of setDefault method, of class MServletMapper.
     */
    @Test
    public void testSetDefault() {
        System.out.println("=========testSetDefault===========");
        HServlet s1 = Mockito.mock(HServlet.class);
        this.servlet.setDefaultServlet(s1);
        Assert.assertEquals(s1, this.servlet.getServlet("/example/res.txt"));
    }

    @Test
    public void testProcessNotFound() throws Exception {
        System.out.println("==============HServletMap:testProcessNotFound================");
        HMessage request = HMessage.createRequest(HMethods.GET, new URI("/hello"), "localhost:4848");
        HMessage response = this.servlet.handle(request);
        System.out.println(response);
        Assert.assertEquals(HCodes.NOT_FOUND, ((ResponseLine) response.line).code);
    }

    @Test
    public void testProcessError() throws Exception {
        System.out.println("==============HServletMap:testProcessError================");
        HServlet errorServlet = Mockito.mock(HServlet.class);
        Mockito.when(errorServlet.handle(Mockito.any(HMessage.class))).thenThrow(new HException("Test error"));
        this.servlet.addServlet("/hello", errorServlet);
        HMessage request = HMessage.createRequest(HMethods.GET, new URI("/hello"), "localhost:4848");
        HMessage response = this.servlet.handle(request);
        System.out.println(response);
        Assert.assertEquals(HCodes.INTERNAL_ERROR, ((ResponseLine) response.line).code);
    }

    @Test
    public void testLongestWildcardPath() throws Exception {
        System.out.println("==============HServletMap:testLongestWildcardPath================");
        HServlet shortest = Mockito.mock(HServlet.class);
        HServlet longest = Mockito.mock(HServlet.class);
        HServlet exact = Mockito.mock(HServlet.class);
        HServlet def = Mockito.mock(HServlet.class);

        HServletMap sm = new HServletMap();
        sm.addServlet("/test/*", shortest);
        sm.addServlet("/test/test1/*", longest);
        sm.addServlet("/test/test1/exact", exact);
        sm.setDefaultServlet(def);
        Assert.assertEquals(exact, sm.getServlet("/test/test1/exact"));
        Assert.assertEquals(longest, sm.getServlet("/test/test1/abc"));
        Assert.assertEquals(shortest, sm.getServlet("/test/test12"));
        Assert.assertEquals(def, sm.getServlet("/testt"));
        Assert.assertEquals(shortest, sm.getServlet("/test/"));
        Assert.assertEquals(shortest, sm.getServlet("/test"));
    }

}
