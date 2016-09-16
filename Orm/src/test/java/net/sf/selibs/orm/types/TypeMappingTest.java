/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.types;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import net.sf.selibs.orm.Connector;
import net.sf.selibs.orm.types.TypeMapping.DbType;
import net.sf.selibs.orm.types.TypeMapping.DbTypes;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author root
 */
public class TypeMappingTest {

    Connection con;

    public TypeMappingTest() {
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
    public void tearDown() throws SQLException {
        con.close();
    }

    @Test
    public void testGetPostgresqlMappings() throws Exception {
        System.out.println("===========TypeMappingLoaderTest:testGetPostgresqlMappings=============");
        List<DbType> types = TypeMapping.getTypeMapping(con);
        for (DbType type : types) {
            System.out.println(type);
        }
        Serializer persister = new Persister();
        DbTypes dbTypes = new DbTypes();
        dbTypes.types = types;
        persister.write(dbTypes, new File("postgres_types.xml"));

    }
    @Test
    public void testGetSqlTypeNames() throws Exception {
         System.out.println("===========TypeMappingLoaderTest:testGetSqlTypeNames=============");
         Map<Integer,String> types = TypeMapping.getSqlTypeNames();
         Assert.assertEquals("ARRAY", types.get(Types.ARRAY));
         Assert.assertEquals("BIT", types.get(Types.BIT));
    }
    @Test
    public void testGetByDbTypeName() throws Exception {
         System.out.println("===========TypeMappingLoaderTest:testGetByDbTypeName=============");
         List<DbType> types = TypeMapping.getTypeMapping(con);
         DbTypes dbTypes = new DbTypes();
         dbTypes.types=types;
         DbType type = dbTypes.getByDbTypeName("text");
         System.out.println(type);
         Assert.assertEquals("text", type.dbType);
    }

}
