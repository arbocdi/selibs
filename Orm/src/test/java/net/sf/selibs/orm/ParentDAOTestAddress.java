package net.sf.selibs.orm;

import net.sf.selibs.orm.ParentDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
public class ParentDAOTestAddress {
     static Connection con;
    ParentDAO dao;


    @BeforeClass
    public static void setUpClass() throws IOException, SQLException {
        Connector connector = new Connector();
        con = Connector.getConnection();
        
    }

    @AfterClass
    public static void tearDownClass() throws IOException, SQLException {

    }

    @Before
    public void setUp() throws SQLException, DataAccessException {
        Statement stmt = con.createStatement();
        String createTable = "CREATE TABLE IF NOT EXISTS  address "
                + "(city text,"
                + "street text)";
        stmt.execute(createTable);
        stmt.close();
        dao = new ParentDAO(Address.class, new SQLGenerator());
        dao.setCon(con);
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
        System.out.println("===========ParentDAOTestAddress:testInsert===========");
        Address address = new Address();
        address.city="Bishkek";
        address.street="Mira";
        dao.insert(address);
        Address fromDb = (Address) dao.findAll().get(0);
        System.out.println(fromDb);
        Assert.assertEquals(address, fromDb);
    }

    /**
     * Test of insertReturning method, of class ParentDAO.
     */
    @Test
    public void testInsertReturning() throws Exception {
        System.out.println("===========ParentDAOTestAddress:testInsertReturning===========");
        Address address = new Address();
        address.city="Bishkek";
        address.street="Mira";
        dao.insertReturnPK(address);
        Address fromDb = (Address) dao.findAll().get(0);
        System.out.println(fromDb);
        Assert.assertEquals(address, fromDb);

    }

    /**
     * Test of updateByPk method, of class ParentDAO.
     */
    @Test (expected = DataAccessException.class)
    public void testUpdate() throws Exception {
        System.out.println("===========ParentDAOTestAddress:testUpdate===========");
        Address address = new Address();
        address.city="Bishkek";
        address.street="Mira";
        dao.insertReturnPK(address);
        dao.update(address);
    }

    /**
     * Test of updateColumnsByPk method, of class ParentDAO.
     */
   @Test (expected = DataAccessException.class)
    public void testUpdateSpecifiedColumns() throws Exception {
        System.out.println("===========ParentDAOTestAddress:testUpdateSpecifiedColumns===========");
         Address address = new Address();
        address.city="Bishkek";
        address.street="Mira";
        dao.insertReturnPK(address);
        dao.updateSpecifiedColumns(address,"street");
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
        System.out.println("===========ParentDAOTestAddress:testFindWhere===========");
        Address address = new Address();
        address.city="Bishkek";
        address.street="Mira";
        dao.insert(address);
        Address fromDb = (Address) dao.findWhere("city = ?", address.city).get(0);
        System.out.println(fromDb);
        Assert.assertEquals(address, fromDb);
       
    }
    @Test
    public void testFindWhereSingle() throws Exception {
        System.out.println("===========ParentDAOTestAddress:testFindWhereSingle===========");
        Address address = new Address();
        address.city="Bishkek";
        address.street="Mira";
        dao.insert(address);
        Address fromDb = (Address) dao.findWhereSingle("city = ?", address.city);
        System.out.println(fromDb);
        Assert.assertEquals(address, fromDb);
    }

}
