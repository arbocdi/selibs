/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.properties;

import net.sf.selibs.orm.properties.PKProperties;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.orm.UserID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author selibs
 */
public class PKPropertiesTest {

    PKProperties props;

    public PKPropertiesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        props = new PKProperties(UserID.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getPkClass method, of class PKProperties.
     */
    @Test
    public void testGetPkClass() {
        System.out.println("========PKProperties:testGetPkClass==========");
        Assert.assertEquals(UserID.class, this.props.getPkClass());
    }

    /**
     * Test of getFieldNames method, of class PKProperties.
     */
    @Test
    public void testGetFieldNames() {
        System.out.println("========PKProperties:testGetFieldNames==========");
        List<String> names = new LinkedList();
        names.add("user_id");
        Assert.assertEquals(names, this.props.tableColumns.readableNames);
    }

}
