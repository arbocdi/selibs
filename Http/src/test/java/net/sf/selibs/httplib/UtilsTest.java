/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.httplib;

import net.sf.selibs.http.Utils;
import junit.framework.Assert;
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
public class UtilsTest {
    
    public UtilsTest() {
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
     * Test of removePrefixTabs method, of class Utils.
     */
    @Test
    public void testRemovePrefixTabs() {
        System.out.println("============Utils:testRemovePrefixTabs==============");
        String sample = "       hello";
        String result = Utils.removeWhiteSpaces(sample);
        System.out.println(result);
        Assert.assertEquals("hello", result);
    }
    
}
