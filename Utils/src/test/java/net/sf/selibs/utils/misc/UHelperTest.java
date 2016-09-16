/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.misc;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.sf.selibs.utils.cloning.NotCloneable;
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
public class UHelperTest {

    public UHelperTest() {
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
    public void testClose() {
        System.out.println("===============UHelper:testClose==============");
        CloseTest ct = new CloseTest();
        UHelper.close(ct);
        Assert.assertTrue(ct.closed);
    }

    public static class CloseTest {

        public boolean closed = false;

        public void close() {
            closed = true;
        }
    }

    @Test
    public void testClone() throws Exception {
        System.out.println("===============UHelper:testClone==============");
        Car myCar = new Car();
        myCar.maxSpeed = 100;
        myCar.name = "my shiny car";
        myCar.year = 2016;
        myCar.setOwner("arbocdi");

        Car clone = UHelper.cloneReflection(myCar);
        System.out.println(myCar);
        System.out.println(clone);

        Assert.assertEquals(myCar.maxSpeed, clone.maxSpeed);
        Assert.assertEquals(myCar.year, clone.year);
        Assert.assertEquals(myCar.getOwner(), clone.getOwner());
        Assert.assertNull(clone.name);

    }

    @Test
    public void testDeepClone() throws Exception {
        System.out.println("===============UHelper:testDeepClone==============");
        Car c = new Car();
        c.maxSpeed = 1000;
        c.name = "volga";
        Car clone = UHelper.cloneSerializeation(c);
        System.out.println(c);
        System.out.println(clone);
        Assert.assertEquals(clone, c);
    }

    @Data
    public static class Mobile implements Serializable {

        public int maxSpeed;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class Car extends Mobile {

        @NotCloneable
        protected String name;
        protected int year;
        private String owner;
    }

}
