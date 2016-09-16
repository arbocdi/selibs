/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.velocity_ui;

import net.sf.selibs.velocity_ui.VelocityUtils;
import java.io.StringWriter;
import junit.framework.Assert;
import net.sf.selibs.velocity_ui.VelocityUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
public class VelocityUtilsTest {
    
    public VelocityUtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.configure();
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
    public void testCreate() throws Exception {
        System.out.println("=========VEFactoryTest:testCreate===========");
        VelocityEngine ve = VelocityUtils.createEngine();
        Template hello = ve.getTemplate("hello");
        VelocityContext ctx = new VelocityContext();
        ctx.put("name", "world");
        StringWriter sw = new StringWriter();
        hello.merge(ctx, sw);
        System.out.println(sw);
        Assert.assertEquals("Hello world", sw.toString());
        
    }
    
}
