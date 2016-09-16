/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm;

import net.sf.selibs.orm.ParentDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.orm.spec.DataAccessException;
import net.sf.selibs.orm.sql.SQLGenerator;
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
public class ParentDAOTest {

    static Connection con;
    ParentDAO dao;


    @BeforeClass
    public static void setUpClass() throws IOException, SQLException {
        Connector connector = new Connector();
        con = Connector.getConnection();
        String createSchema = "CREATE SCHEMA data";
        Statement stmt = con.createStatement();
        try {
            stmt.execute(createSchema);
        } catch (Exception ignore) {
        }

    }

    @AfterClass
    public static void tearDownClass() throws IOException, SQLException {
         
    }

    @Before
    public void setUp() throws SQLException, DataAccessException {
        Statement stmt = con.createStatement();
        String createTable = "CREATE TABLE IF NOT EXISTS  data.users "
                + "(user_id serial NOT NULL PRIMARY KEY,"
                + "name text,"
                + "password text,"
                + "nonInsertable text DEFAULT 'Hello',"
                + "nonUpdatable text)";
        stmt.execute(createTable);
        stmt.close();
        dao = new ParentDAO(User.class, new SQLGenerator());
        dao.setCon(con);
        dao.setShowSql(true);
        dao.clearTable();

    }

    @After
    public void tearDown() throws SQLException {
    }

    /**
     * Test of insert method, of class ParentDAO.
     */
    @Test
    public void testInsert() throws Exception {
        System.out.println("===========ParentDAO:testInsert===========");
        User user = new User();
        user.setPassword("123");
        user.username = "me";
        this.dao.insert(user);
        List<User> users = dao.findAll();
        System.out.println(users);
        User userDb = users.get(0);
        Assert.assertEquals(user.getPassword(), userDb.getPassword());
        Assert.assertEquals(user.username, userDb.username);
        Assert.assertNotNull(userDb.userId);
    }
     @Test
    public void testBatchInsert() throws Exception {
        System.out.println("===========ParentDAO:testBatchInsert===========");
        User user = new User();
        user.password = "123";
        user.username = "me";
        
        User user2 = new User();
        user2.password = "1234";
        user2.username=null;
        
        List<User> batch = new LinkedList();
        batch.add(user);
        batch.add(user2);
        batch.add(user);
        this.dao.batchInsert(batch);
        List<User> users = dao.findAll();
        for(User u:users){
            System.out.println(u);
        }
        User userDb = users.get(0);
        Assert.assertEquals(user.password, userDb.password);
        Assert.assertEquals(user.username, userDb.username);
        Assert.assertNotNull(userDb.userId);
        Assert.assertEquals(3, users.size());
    
    }
     @Test
    public void testSafeBatchInsert() throws Exception {
        System.out.println("===========ParentDAO:testSafeBatchInsert===========");
        User user = new User();
        user.password = "123";
        user.username = "me";
        
        User user2 = new User();
        user2.password = "1234";
        user2.username=null;
        
        List<User> batch = new LinkedList();
        batch.add(user);
        batch.add(user2);
        batch.add(user);
        this.dao.safeBatchInsert(batch,100);
        List<User> users = dao.findAll();
        for(User u:users){
            System.out.println(u);
        }
        User userDb = users.get(0);
        Assert.assertEquals(user.password, userDb.password);
        Assert.assertEquals(user.username, userDb.username);
        Assert.assertNotNull(userDb.userId);
        Assert.assertEquals(3, users.size());
    }
    
    

    /**
     * Test of insertReturning method, of class ParentDAO.
     */
    @Test
    public void testInsertReturning() throws Exception {
        System.out.println("===========ParentDAO:testInsertReturning===========");
        User user = new User();
        user.password = "123";
        user.username = "me";
        this.dao.insertReturnPK(user);
        System.out.println(user);
        User userDb = (User) dao.find(user.userId);
        Assert.assertEquals(user.password, userDb.password);
        Assert.assertEquals(user.username, userDb.username);
        Assert.assertNotNull(userDb.userId);

    }
    @Test
    public void testRemove() throws Exception {
        System.out.println("===========ParentDAO:testRemove===========");
        User user = new User();
        user.password = "123";
        user.username = "me";
        this.dao.insertReturnPK(user);
        System.out.println(user);
        dao.remove(user);
        User userDb = (User) dao.find(user.userId);
        Assert.assertNull(userDb);
    }

    /**
     * Test of updateByPk method, of class ParentDAO.
     */
    @Test
    public void testUpdate() throws Exception {
        System.out.println("===========ParentDAO:testUpdate===========");
        User user = new User();
        user.password = "123";
        user.username = "me";
        this.dao.insertReturnPK(user);
        user.username = "arboc";
        this.dao.update(user);
        User fromDb = (User) dao.find(user.userId);
        Assert.assertEquals(user.password, fromDb.password);
        Assert.assertEquals(user.username, fromDb.username);
    }

    /**
     * Test of updateColumnsByPk method, of class ParentDAO.
     */
    @Test
    public void testUpdateSpecifiedColumns() throws Exception {
        System.out.println("===========ParentDAO:testUpdateSpecifiedColumns===========");
        User user = new User();
        user.password = "123";
        user.username = "me";
        this.dao.insertReturnPK(user);
        user.username = "arboc";
        user.password = "345";
        this.dao.updateSpecifiedColumns(user, "name,password");
        User fromDb = (User) dao.find(user.userId);
        Assert.assertEquals(user.password, fromDb.password);
        Assert.assertEquals(user.username, fromDb.username);

    }

    /**
     * Test of clearTable method, of class ParentDAO.
     */
    @Test
    public void testClearTable() throws Exception {

    }

    /**
     * Test of dropTable method, of class ParentDAO.
     */
    @Test
    public void testDropTable() throws Exception {
        System.out.println("===========ParentDAO:testDropTable===========");
        System.out.println(dao.dropTable());

    }
    @Test
    public void testFindWhere() throws Exception {
        System.out.println("===========ParentDAO:testFindWhere===========");
        User user = new User();
        user.password = "123";
        user.username = "me";
        this.dao.insertReturnPK(user);
        User fromDb = (User) dao.findWhere("name = ?",user.username).get(0);
        Assert.assertEquals(user.password, fromDb.password);
        Assert.assertEquals(user.username, fromDb.username);
    }
    @Test
    public void testFindWhereSingle() throws Exception {
        System.out.println("===========ParentDAO:testFindWhereSingle===========");
        User user = new User();
        user.password = "123";
        user.username = "me";
        this.dao.insert(user);
        this.dao.insert(user);
        User fromDb = (User) dao.findWhereSingle("name = ?",user.username);
        Assert.assertEquals(user.password, fromDb.password);
        Assert.assertEquals(user.username, fromDb.username);
    }

}
