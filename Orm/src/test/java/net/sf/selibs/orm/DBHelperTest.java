/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm;

import java.sql.Connection;
import java.sql.SQLException;
import net.sf.selibs.utils.misc.UHelper;
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
public class DBHelperTest {
    
    protected Connection con;
    
    public DBHelperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws SQLException {
        con = Connector.getDS().getConnection();
    }
    
    @After
    public void tearDown() {
        UHelper.close(con);
    }

    @Test
    public void testCheckStatus() throws Exception{
        System.out.println("==========DBHelperTest:testCheckStatus==========");
        DBHelper.checkStatus(con, "SELECT 1");
    }
    
}
