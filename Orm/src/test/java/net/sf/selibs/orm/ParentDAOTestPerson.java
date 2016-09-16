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


public class ParentDAOTestPerson {
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
        String createTable = "CREATE TABLE IF NOT EXISTS  person "
                + "(id serial NOT NULL,"
                + "uid text NOT NULL,"
                + "firstName text,"
                + "lastName text,"
                + "PRIMARY KEY (id,uid))";
        stmt.execute(createTable);
        stmt.close();
        dao = new ParentDAO(Person.class, new SQLGenerator());
        dao.setCon(con);
        dao.clearTable();

    }

    @After
    public void tearDown() throws SQLException {
    }
   
    @Test
    public void testInsert() throws Exception {
        System.out.println("===========ParentDAOTestPerson:testInsert===========");
        Person person = new Person();
        person.id = new PersonID();
        person.id.uid="test uid";
        person.firstName="arboc";
        person.lastName = "digambara";
        dao.insert(person);
        Person personDb = (Person) dao.findAll().get(0);
        Assert.assertEquals(person.firstName, personDb.firstName);
        Assert.assertEquals(person.lastName, personDb.lastName);
        Assert.assertEquals(person.id.uid, personDb.id.uid);
        Assert.assertNotNull(personDb.id.id);
       
    }

   
    @Test
    public void testInsertReturning() throws Exception {
        System.out.println("===========ParentDAOTestPerson:testInsertReturning===========");
        Person person = new Person();
        person.id = new PersonID();
        person.id.uid="test uid";
        person.firstName="arboc";
        person.lastName = "digambara";
        dao.insertReturnPK(person);
        Assert.assertEquals("test uid", person.id.uid);
        Assert.assertNotNull(person.id.id);
    }

    /**
     * Test of updateByPk method, of class ParentDAO.
     */
    @Test
    public void testUpdate() throws Exception {
        System.out.println("===========ParentDAOTestPerson:testUpdate===========");
        Person person = new Person();
        person.id = new PersonID();
        person.id.uid="test uid";
        person.firstName="arboc";
        person.lastName = "digambara";
        dao.insertReturnPK(person);
        person.lastName="cobra";
        dao.update(person);
        Person fromDb = (Person) dao.find(person.id);
        Assert.assertEquals(person.firstName,fromDb.firstName);
        Assert.assertEquals(person.id.uid,fromDb.id.uid);
        Assert.assertEquals(person.lastName,fromDb.lastName);
        Assert.assertNotNull(person.id.id);}
    /**
     * Test of updateColumnsByPk method, of class ParentDAO.
     */
    @Test
    public void testUpdateSpecifiedColumns() throws Exception {
        System.out.println("===========ParentDAOTestPerson:testUpdateSpecifiedColumns===========");
        Person person = new Person();
        person.id = new PersonID();
        person.id.uid="test uid";
        person.firstName="arboc";
        person.lastName = "digambara";
        dao.insertReturnPK(person);
        person.lastName="cobra";
        person.firstName="dhurva";
        dao.updateSpecifiedColumns(person, "lastname,firstname");
        Person fromDb = (Person) dao.find(person.id);
        Assert.assertEquals(person.firstName,fromDb.firstName);
        Assert.assertEquals(person.lastName,fromDb.lastName);
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
       System.out.println("===========ParentDAOTestPerson:testFindWhere===========");
        Person person = new Person();
        person.id = new PersonID();
        person.id.uid="test uid";
        person.firstName="arboc";
        person.lastName = "digambara";
        dao.insertReturnPK(person);
        Person fromDb = (Person) dao.findWhere("firstname = ?", person.firstName).get(0);
        Assert.assertEquals(person, fromDb);
    }
    @Test
    public void testFindWhereSingle() throws Exception {
        System.out.println("===========ParentDAO:testFindWhereSingle===========");
         Person person = new Person();
        person.id = new PersonID();
        person.id.uid="test uid";
        person.firstName="arboc";
        person.lastName = "digambara";
        dao.insertReturnPK(person);
        Person fromDb = (Person) dao.findWhereSingle("firstname = ?", person.firstName);
        Assert.assertEquals(person, fromDb);
    }

}
