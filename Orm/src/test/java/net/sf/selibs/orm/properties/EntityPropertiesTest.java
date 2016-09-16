/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.properties;

import net.sf.selibs.orm.properties.EntityProperties;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.orm.User;
import net.sf.selibs.orm.sql.SQLGenerator;
import net.sf.selibs.orm.types.TypeMapping.DbType;
import net.sf.selibs.orm.types.TypeMapping.DbTypes;
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
public class EntityPropertiesTest {
    
    EntityProperties props;
    
    public EntityPropertiesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.props = new EntityProperties(User.class, new SQLGenerator());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getInsert method, of class EntityProperties.
     */
    @Test
    public void testGetInsert() {
        System.out.println("============EntityProperties:testGetInsert=============");
        //INSERT INTO table (col1,col2...) VALUES(?,...,?)
        String sql = this.props.getInsert();
        System.out.println(sql);
        Assert.assertEquals("INSERT INTO data.users (name,password,nonUpdatable)\nVALUES (?,?,?)", sql);
    }

    /**
     * Test of getInsertReturning method, of class EntityProperties.
     */
    @Test
    public void testGetInsertReturning() {
        System.out.println("============EntityProperties:testGetInsertReturning=============");
        //INSERT INTO table (col1,col2...) VALUES(?,...,?) RETURNING idCol1,idCol2...
        String sql = this.props.getInsertReturning();
        System.out.println(sql);
        Assert.assertEquals("INSERT INTO data.users (name,password,nonUpdatable)\nVALUES (?,?,?) RETURNING user_id", sql);
    }

    /**
     * Test of getUpdateByPKSet method, of class EntityProperties.
     */
    @Test
    public void testGetUpdateByPKSet() {
        System.out.println("============EntityProperties:testGetUpdateByPKSet=============");
        //UPDATE table SET _set_ WHERE pkCol1=? AND pkCol2=?...
        String sql = this.props.getUpdateByPKSet("name=?");
        System.out.println(sql);
        Assert.assertEquals("UPDATE data.users\nSET name=?\nWHERE user_id = ? ", sql);
    }

    /**
     * Test of getUpdateByPK method, of class EntityProperties.
     */
    @Test
    public void testGetUpdateByPK() {
        System.out.println("============EntityProperties:testGetUpdateByPK=============");
        //UPDATE table SET col1=?,col2=?... WHERE pkCol1=? AND pkCol2=?...
        String sql = this.props.getUpdateByPK();
        System.out.println(sql);
        Assert.assertEquals("UPDATE data.users\nSET name = ?,password = ?,nonInsertable = ?\nWHERE user_id = ? ", sql);
    }

    /**
     * Test of getReadByCondition method, of class EntityProperties.
     */
    @Test
    public void testGetReadByCondition() {
        System.out.println("============EntityProperties:testGetReadByCondition=============");
        //SELECT (col1,col2...) FROM table WHERE condition
        String sql = this.props.getSelectWithCondition("name=?");
        System.out.println(sql);
        Assert.assertEquals("SELECT name,password,nonInsertable,nonUpdatable,user_id\nFROM data.users tbl\nWHERE name=?", sql);
    }

    /**
     * Test of getReadByCondition method, of class EntityProperties.
     */
    @Test
    public void testGetSelect() {
        System.out.println("============EntityProperties:testGetSelect=============");
        //SELECT (col1,col2...) FROM table
        String sql = this.props.getSelect();
        System.out.println(sql);
        Assert.assertEquals("SELECT name,password,nonInsertable,nonUpdatable,user_id\nFROM data.users tbl", sql);
    }

    /**
     * Test of getReadByPK method, of class EntityProperties.
     */
    @Test
    public void testGetReadByPK() {
        System.out.println("============EntityProperties:testGetReadByPK()=============");
        //SELECT (col1,...,colN) FROM table WHERE pkCol1=? AND pkCol2=?...
        String sql = this.props.getReadByPK();
        System.out.println(sql);
        Assert.assertEquals("SELECT name,password,nonInsertable,nonUpdatable,user_id\nFROM data.users tbl\nWHERE user_id = ? ", sql);
    }

    /**
     * Test of getUpdatableFields method, of class EntityProperties.
     */
    @Test
    public void testGetUpdatableFields_List() {
        System.out.println("============EntityProperties:testGetUpdatableFields_List=============");
        List<String> names = new LinkedList();
        names.add("name");
        names.add("password");
        List<Field> fields = this.props.getAllUpdatableFields(names);
        List<String> fNames = ClassUtils.getColumnNames(fields);
        Assert.assertTrue(fNames.contains("name"));
        Assert.assertTrue(fNames.contains("password"));
    }

    @Test
    public void testGetRemove() {
        System.out.println("============EntityProperties:testGetRemove=============");
        String sql = this.props.getRemove();
        System.out.println(sql);
        Assert.assertEquals("DELETE FROM data.users\n"
                + "WHERE user_id = ? ", sql);
    }
    @Test
    public void testGetDbTypes() throws Exception {
        System.out.println("==========EntityProperties:testGetDbTypes===========");
        List<Field> fields = this.props.getAllInsertableFields();
        for(Field f:fields){
            System.out.println(f);
        }
        List<DbType> types = this.props.getDbTypes(fields);
        for(DbType type:types){
            System.out.println(type);
        }
        Assert.assertEquals("text", types.get(0).dbType);
        Assert.assertEquals("text", types.get(1).dbType);
        Assert.assertNull(types.get(2));
        Assert.assertEquals(fields.size(), types.size());
        
    }
    
}
