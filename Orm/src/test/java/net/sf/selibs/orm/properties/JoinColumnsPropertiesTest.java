/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.properties;

import java.lang.reflect.Field;
import javax.persistence.JoinColumns;
import net.sf.selibs.orm.relations.Planet;
import net.sf.selibs.orm.relations.SolarSystem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author root
 */
public class JoinColumnsPropertiesTest {

    public JoinColumnsPropertiesTest() {
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
     * Test of getFromClass method, of class JoinsProperties.
     */
    @Test
    public void testGetFromClass() {
    }

    /**
     * Test of getFromField method, of class JoinsProperties.
     */
    @Test
    public void testGetFromField() {
        System.out.println("==============JoinColumnsProperties:testGetFromField================");
        Field system = ClassUtils.getAnnotatedField(Planet.class, JoinColumns.class);
        JoinsProperties jcProps = JoinsProperties.getFromField(system);
        System.out.println(jcProps);
        Assert.assertEquals(2, jcProps.joinColumns.size());
        Assert.assertEquals(SolarSystem.class, jcProps.clazz);
        Assert.assertEquals(system, jcProps.field);

        JoinProperties sysId = jcProps.joinColumns.get(0);
        Assert.assertEquals("sys_id", sysId.name);
        Assert.assertEquals("id", sysId.refColName);

        JoinProperties sysUid = jcProps.joinColumns.get(1);
        Assert.assertEquals("sys_uid", sysUid.name);
        Assert.assertEquals("uid", sysUid.refColName);

    }

    /**
     * Test of getSQLCondition method, of class JoinsProperties.
     */
    @Test
    public void testGetSQLCondition() {
        System.out.println("==============JoinColumnsProperties:testGetSQLCondition================");
        JoinsProperties jCols = new JoinsProperties();
        
        JoinProperties jCol1 = new JoinProperties();
        jCol1.refColName = "id";
        
        JoinProperties jCol2 = new JoinProperties();
        jCol2.refColName = "uid";
        
        jCols.joinColumns.add(jCol1);
        jCols.joinColumns.add(jCol2);
        
        String sql = jCols.getSQLCondition();
        System.out.println(sql);
        Assert.assertEquals(" id = ? AND uid = ? ", sql);

    }

}
