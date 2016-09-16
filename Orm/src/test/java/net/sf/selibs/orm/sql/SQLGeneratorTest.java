package net.sf.selibs.orm.sql;

import net.sf.selibs.orm.sql.SQLGenerator;
import net.sf.selibs.orm.sql.SQLTable;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.orm.properties.PKProperties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author selibs
 */
public class SQLGeneratorTest {

    SQLTable table;
    SQLGenerator gen;
    PKProperties pkProps;

    public SQLGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        table = new SQLTable("users");
        gen = new SQLGenerator(table);
        pkProps = Mockito.mock(PKProperties.class);
        List<String> pkFieldNames = new LinkedList();
        pkFieldNames.add("id");
        Mockito.when(pkProps.getFieldNames()).thenReturn(pkFieldNames);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDeleteWhere method, of class SQLGenerator.
     */
    @Test
    public void testGetDeleteWhere() {
        System.out.println("==============SQLGenerator:getDeleteWhere==============");
        //DELETE FROM table_name WHERE condition_for_prepared_statement
        String sql = this.gen.getDeleteWhere(" id = 10");
        System.out.println(sql);
        Assert.assertEquals("DELETE FROM users\nWHERE  id = 10", sql);

    }

    /**
     * Test of getInsert method, of class SQLGenerator.
     */
    @Test
    public void testGetInsert() {
        System.out.println("==============SQLGenerator:testGetInsert==============");
        //INSERT INTO table_name (col1,...colN) VALUES (?,...,?)
        List<String> columns = new LinkedList();
        columns.add("name");
        columns.add("pw");
        String sql = gen.getInsert(columns);
        System.out.println(sql);
        Assert.assertEquals("INSERT INTO users (name,pw)\nVALUES (?,?)", sql);
    }

    /**
     * Test of getInsertReturning method, of class SQLGenerator.
     */
    @Test
    public void testGetInsertReturning() {
        System.out.println("==============SQLGenerator:testGetInsertReturning==============");
        //INSERT INTO table_name (col1,...colN) VALUES (?,...,?) RETURNING pk1,pk2,...
        List<String> columns = new LinkedList();
        columns.add("name");
        columns.add("pw");

        List<String> pkCols = new LinkedList();
        pkCols.add("id");

        String sql = gen.getInsertReturning(columns, pkCols);
        System.out.println(sql);
        Assert.assertEquals("INSERT INTO users (name,pw)\nVALUES (?,?) RETURNING id", sql);

    }

    /**
     * Test of getUpdateWhereSet method, of class SQLGenerator.
     */
    @Test
    public void testGetUpdateWhereSet() {
        System.out.println("==============SQLGenerator:testGetUpdateWhereSet==============");
        //UPDATE table_name SET _set_ WHERE _where_
        String sql = gen.getUpdateWhereSet("id>5", "name=null");
        System.out.println(sql);
        Assert.assertEquals("UPDATE users\nSET name=null\nWHERE id>5", sql);
    }

    /**
     * Test of getUpdatePKSet method, of class SQLGenerator.
     */
    @Test
    public void testGetUpdatePKSet() {
        System.out.println("==============SQLGenerator:testGetUpdatePKSet==============");
        //UPDATE table_name SET _set_ WHERE pkCol1=? AND pkCol2=?...
        String sql = gen.getUpdatePKSet(pkProps.getFieldNames(), "name=arbodi");
        System.out.println(sql);
    }

    /**
     * Test of getDropTable method, of class SQLGenerator.
     */
    @Test
    public void testGetDropTable() {
        System.out.println("==============SQLGenerator:testGetDropTable==============");
        //DROP TABLE table_name
        String sql = gen.getDropTable();
        System.out.println(sql);
        Assert.assertEquals("DROP TABLE users CASCADE", sql);
    }

    /**
     * Test of getClearTable method, of class SQLGenerator.
     */
    @Test
    public void testGetClearTable() {
        System.out.println("==============SQLGenerator:testGetClearTable==============");
        //DELETE FROM table_name
        String sql = gen.getClearTable();
        System.out.println(sql);
        Assert.assertEquals("DELETE FROM users", sql);
    }

    /**
     * Test of getReadByCondition method, of class SQLGenerator.
     */
    @Test
    public void testGetReadByCondition() {
        System.out.println("==============SQLGenerator:testGetReadByCondition==============");
        //SELECT (col1,col2...) FROM table WHERE condition
        List<String> columns = new LinkedList();
        columns.add("name");
        columns.add("pw");
        String sql = gen.getSelectWithCondition(columns, "pw = ?");
        System.out.println(sql);
        Assert.assertEquals("SELECT name,pw\nFROM users tbl\nWHERE pw = ?", sql);
    }

    @Test
    public void testAddTableName() {
        System.out.println("==============SQLGenerator:testAddTableName==============");
        List<String> columns = new LinkedList();
        columns.add("name");
        columns.add("pw");
        List<String> fullNames = this.gen.appendTableName(columns);
        Assert.assertTrue(fullNames.contains("users.pw"));
        Assert.assertTrue(fullNames.contains("users.name"));
    }

}
