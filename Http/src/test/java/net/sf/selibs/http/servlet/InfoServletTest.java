/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.http.servlet;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import net.sf.selibs.http.HMessage;
import net.sf.selibs.http.constants.HMethods;
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
public class InfoServletTest {
    
    public InfoServletTest() {
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
    public void testPrintServlets() throws Exception {
         System.out.println("==============InfoServletTest:testPrintServlets================");
         Map<String,HServlet> servlets = new HashMap();
         HServlet s1 = new EchoServlet();
         HServlet s2 = new EchoServlet();
         servlets.put("/e1", s1);
         servlets.put("/e2", s2);
         
         
         HMessage request = HMessage.createRequest(HMethods.GET, new URI("/hello"), "localhost:4848");
         request.attachements.put(HServletMap.SERVLETS, servlets);
         
         InfoServlet servlet = new InfoServlet();
         
         HMessage response = servlet.handle(request);
         
         String html = new String(response.payload,"UTF-8");
         System.out.println(html);
         Assert.assertTrue(html.contains("/e1"));
         Assert.assertTrue(html.contains("/e2"));
    }
    
}
