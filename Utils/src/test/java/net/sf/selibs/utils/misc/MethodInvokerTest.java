/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.misc;

import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;

/**
 *
 * @author root
 */
public class MethodInvokerTest {

    public MethodInvokerTest() {
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
    public void testInvokeVoid() throws Exception {
        System.out.println("==============MethodInvokerTest:testInvokeVoid===============");
        String p1 = "Hello";
        Object[] params = {p1};
        SomeClass instance = new SomeClass();
        Object result = MethodInvoker.invoke(instance, "append", params);
        Assert.assertEquals(p1, instance.params.get(0));
        Assert.assertNull(result);
    }

    @Test
    public void testInvokeAdd() throws Exception {
        System.out.println("==============MethodInvokerTest:testInvokeAdd===============");
        Object[] params = {10, 12};
        SomeClass instance = new SomeClass();
        int result = (Integer) MethodInvoker.invoke(instance, "add", params);
        Assert.assertEquals(22, result);
    }

    @Test
    public void testInvokeAddExplicitSignature() throws Exception {
        System.out.println("==============MethodInvokerTest:testInvokeAddExplicitSignature===============");
        Object[] params = {10, 12};
        Class[] classes = {int.class, int.class};
        SomeClass instance = new SomeClass();
        int result = (Integer) MethodInvoker.invoke(instance, "add", classes, params);
        Assert.assertEquals(32, result);
    }
    @Test
    public void testInvokeNoParams() throws Exception {
        System.out.println("==============MethodInvokerTest:testInvokeAddExplicitSignature===============");
        Object[] params = {};
        Class[] classes = {};
        SomeClass instance = Mockito.spy(new SomeClass());
        Object result = MethodInvoker.invoke(instance, "method", classes, params);
        Assert.assertNull(result);
        Mockito.verify(instance).method();
    }

    public static class SomeClass {

        public List<String> params = new LinkedList();

        public Integer add(Integer a, Integer b) {
            return a + b;
        }

        public int add(int a, int b) {
            return a + b + 10;
        }

        public void append(String param) {
            params.add(param);
        }
        public void method(){
            
        }
    }

}
