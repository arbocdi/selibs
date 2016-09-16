/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import net.sf.selibs.orm.Connector;
import net.sf.selibs.utils.misc.UHelper;
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
public class DBMapTest {
    
    static Connection con;
    
    Record arboc;
    Record maya;
    
    public DBMapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws SQLException {
        con = Connector.getDS().getConnection();
        DBMap.executeStatement(con, "CREATE TABLE IF NOT EXISTS db_map_test(uname text,upassword text)", QueryType.UPDATE);
    }
    
    @AfterClass
    public static void tearDownClass() {
        UHelper.close(con);
    }
    
    @Before
    public void setUp() throws SQLException {
        DBMap.executeStatement(con, "INSERT INTO db_map_test VALUES(?,?) ", QueryType.UPDATE, "arboc", "123");
        DBMap.executeStatement(con, "INSERT INTO db_map_test VALUES(?,?) ", QueryType.UPDATE, "maya", "345");
        
        arboc = new Record();
        arboc.add("uname", "arboc");
        arboc.add("upassword", "123");
        
        maya = new Record();
        maya.add("uname", "maya");
        maya.add("upassword", "345");
    }
    
    @After
    public void tearDown() throws SQLException {
        DBMap.executeStatement(con, "DELETE FROM db_map_test", QueryType.UPDATE);
    }
    
    @Test
    public void testGetColumnNames() throws Exception {
        System.out.println("========DBMapTest:testGetColumnNames=========");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT * FROM db_map_test");
            rs = stmt.executeQuery();
            Set<String> columnNames = DBMap.getColumnNames(rs);
            System.out.println(columnNames);
            Assert.assertTrue(columnNames.contains("uname"));
            Assert.assertTrue(columnNames.contains("upassword"));
            
        } finally {
            UHelper.close(rs);
            UHelper.close(stmt);
        }
    }
    
    @Test
    public void testGetFromRs() throws Exception {
        System.out.println("========DBMapTest:testGetFromRs=========");
        List<Record> records = (List<Record>) DBMap.executeStatement(con, "SELECT * FROM db_map_test", QueryType.QUERY);
        System.out.println(records);
        Assert.assertTrue(records.contains(this.arboc));
        Assert.assertTrue(records.contains(this.maya));
        Assert.assertEquals(2, records.size());
    }

    @Test
    public void testGetFromRsArboc() throws Exception {
        System.out.println("========DBMapTest:testGetFromRsArboc=========");
        List<Record> records = (List<Record>) DBMap.executeStatement(con, "SELECT * FROM db_map_test WHERE uname = ?", QueryType.QUERY, "arboc");
        System.out.println(records);
        Assert.assertEquals(1, records.size());
        Assert.assertTrue(records.contains(arboc));
    }
    
}
