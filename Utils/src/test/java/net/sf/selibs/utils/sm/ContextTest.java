/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.sm;

import junit.framework.Assert;
import net.sf.selibs.utils.cloning.Cloner;
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
public class ContextTest {

    MessageContext ctx;

    public ContextTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        ctx = new MessageContext();
        ctx.addStates();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGoNext() throws Exception {
        System.out.println("==========Context:testGoNext==========");
        for (int i = 0; i < 1000; i++) {
            MessageContext clone = (MessageContext) Cloner.clone(ctx);
            clone.goNext();

            System.out.println(clone.values);
            Assert.assertEquals("message1", clone.values.get(0));
            Assert.assertEquals("message2", clone.values.get(1));
            Assert.assertEquals("message3", clone.values.get(2));
            Assert.assertEquals("end", clone.values.get(3));
            Assert.assertEquals(4, clone.values.size());
        }
    }

}
