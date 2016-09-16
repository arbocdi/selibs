/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.db_types;

import net.sf.selibs.orm.types.TypeMapping.DbType;
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
public class DbTypesLoaderTest {

    public DbTypesLoaderTest() {
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
    public void testStaticBlock() {
        System.out.println("=========DbTypesLoaderTest:testStaticBlock==========");
        DbType text = DbTypesLoader.getPostgres().getByDbTypeName("text");
        System.out.println(text);
        Assert.assertEquals("text", text.dbType);
    }

}
