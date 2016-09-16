package net.sf.selibs.orm.sql;

import net.sf.selibs.orm.Database;
import net.sf.selibs.orm.ParentDAO;
import net.sf.selibs.orm.relations.Moon;
import net.sf.selibs.orm.relations.Planet;
import net.sf.selibs.orm.relations.SolarSystem;
import net.sf.selibs.orm.relations.Star;
import net.sf.selibs.orm.types.PostgresTypeConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DDLGeneratorTest {

    DDLGenerator ddlGen;
    Database db;

    public DDLGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        db = new Database("net.sf.selibs.orm.relations");
        ddlGen = new DDLGenerator();
        ddlGen.setTypeConverter(new PostgresTypeConverter());

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of makeCreateTable method, of class DDLGenerator.
     */
    @Test
    public void testMakeCreateTableStar() {
        System.out.println("=============DDLGenerator:testMakeCreateTableStar================");
        String sql = this.ddlGen.makeCreateTable(
                ((ParentDAO) db.getTables().get(Star.class)).getEntityProperties(),
                db.getTables());
        System.out.println(sql);
        Assert.assertEquals("CREATE TABLE stars (\n"
                + "name text UNIQUE NOT NULL ,\n"
                + "sys_id int4  NOT NULL ,\n"
                + "sys_uid text  NOT NULL ,\n"
                + "id    serial NOT NULL,\n"
                + "PRIMARY KEY (id),\n"
                + "FOREIGN KEY (sys_id,sys_uid) REFERENCES system(id,uid) ON DELETE CASCADE ON UPDATE CASCADE ,\n"
                + "UNIQUE (sys_id,sys_uid)\n"
                + ")", sql);
    }

    @Test
    public void testMakeCreateTableMoon() {
        System.out.println("=============DDLGenerator:testMakeCreateTableMoon================");
        String sql = this.ddlGen.makeCreateTable(
                ((ParentDAO) db.getTables().get(Moon.class)).getEntityProperties(),
                db.getTables());
        System.out.println(sql);
        Assert.assertEquals("CREATE TABLE moon (\n"
                + "name text   ,\n"
                + "planet_id int4   ,\n"
                + "FOREIGN KEY (planet_id) REFERENCES planet(id) ON DELETE CASCADE ON UPDATE CASCADE \n"
                + ")", sql);
    }

    @Test
    public void testMakeCreateTableSystem() {
        System.out.println("=============DDLGenerator:testMakeCreateTableSystem================");
        String sql = this.ddlGen.makeCreateTable(
                ((ParentDAO) db.getTables().get(SolarSystem.class)).getEntityProperties(),
                db.getTables());
        System.out.println(sql);
        Assert.assertEquals("CREATE TABLE system (\n"
                + "name text   ,\n"
                + "id    serial NOT NULL,\n"
                + "uid text  NOT NULL ,\n"
                + "PRIMARY KEY (id,uid)\n"
                + ")", sql);
    }

    @Test
    public void testMakeCreateTablePlanet() {
        System.out.println("=============DDLGenerator:testMakeCreateTablePlanet================");
        String sql = this.ddlGen.makeCreateTable(
                ((ParentDAO) db.getTables().get(Planet.class)).getEntityProperties(),
                db.getTables());
        System.out.println(sql);
        Assert.assertEquals("CREATE TABLE planet (\n"
                + "name text   ,\n"
                + "sys_id int4   ,\n"
                + "sys_uid text   ,\n"
                + "id    serial NOT NULL,\n"
                + "PRIMARY KEY (id),\n"
                + "FOREIGN KEY (sys_id,sys_uid) REFERENCES system(id,uid) ON DELETE CASCADE ON UPDATE CASCADE \n"
                + ")", sql);

    }

    /**
     * Test of makeColumnDefinition method, of class DDLGenerator.
     */
    @Test
    public void testMakeColumnDefinition() {
    }

    /**
     * Test of makePrimaryKey method, of class DDLGenerator.
     */
    @Test
    public void testMakePrimaryKey() {
    }

    /**
     * Test of makeForeignKey method, of class DDLGenerator.
     */
    @Test
    public void testMakeForeignKey() {
    }

    /**
     * Test of setTypeConverter method, of class DDLGenerator.
     */
    @Test
    public void testSetTypeConverter() {
    }

    /**
     * Test of getTypeConverter method, of class DDLGenerator.
     */
    @Test
    public void testGetTypeConverter() {
    }

}
