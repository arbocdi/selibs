package net.sf.selibs.orm.relations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.selibs.orm.Connector;
import net.sf.selibs.orm.SnakeORMProperties;
import net.sf.selibs.orm.Database;
import net.sf.selibs.orm.JDBCUtils;
import net.sf.selibs.orm.types.PostgresTypeConverter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DatabaseTest2 {

    static Connection con;
    Database database;

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
    public void setUp() throws Exception {
        System.setProperty(SnakeORMProperties.SHOW_DML, "false");
        //this.createTables();
        JDBCUtils.setShowSQL(true);
        database = new Database("net.sf.selibs.orm.relations");
        database.setConnection(con);
        database.setClasses(SolarSystem.class,Star.class,Planet.class,Moon.class);
        database.getDdlGenerator().setTypeConverter(new PostgresTypeConverter());
        System.out.println(database.dropTablesNoException());
        System.out.println(database.createTables());
        //database.clearTables();
        this.addRecords();

    }

    public void addRecords() throws Exception {
        SolarSystem s1 = new SolarSystem();
        s1.name = "solar";
        s1.id = new SystemID();
        s1.id.uid = "123";

        SolarSystem s2 = new SolarSystem();
        s2.name = "mira";
        s2.id = new SystemID();
        s2.id.uid = "123";

        database.insertReturnPK(s1);
        this.database.insertReturnPK(s2);

        Planet p1 = new Planet();
        p1.name = "earth";
        p1.sys_id = s1.id.id;
        p1.sys_uid = s1.id.uid;

        Planet p2 = new Planet();
        p2.name = "mars";
        p2.sys_id = s1.id.id;
        p2.sys_uid = s1.id.uid;

        Planet p3 = new Planet();
        p3.name = "naboo";
        p3.sys_id = s2.id.id;
        p3.sys_uid = s2.id.uid;

        database.insertReturnPK(p1);
        database.insertReturnPK(p2);
        database.insertReturnPK(p3);

        Moon phobos = new Moon();
        phobos.name = "phobos";
        phobos.planet_id = p2.id.id;

        Moon deimos = new Moon();
        deimos.name = "deimos";
        deimos.planet_id = p2.id.id;

        Moon moon = new Moon();
        moon.name = "earth moon";
        moon.planet_id = p1.id.id;

        database.insertReturnPK(phobos);
        database.insertReturnPK(deimos);
        database.insertReturnPK(moon);

    }

    public void createTables() throws SQLException {
        Statement stmt = con.createStatement();
        String createTable = "CREATE TABLE IF NOT EXISTS  system "
                + "(id serial NOT NULL,"
                + " uid text NOT NULL,"
                + " name text,"
                + " PRIMARY KEY (id,uid))";
        stmt.execute(createTable);
        createTable = "CREATE TABLE IF NOT EXISTS  planet "
                + "(id serial NOT NULL PRIMARY KEY ,"
                + " name text ,"
                + " sys_id integer NOT NULL,"
                + " sys_uid text NOT NULL, "
                + " FOREIGN KEY (sys_id,sys_uid) REFERENCES system(id,uid) ON UPDATE CASCADE ON DELETE CASCADE"
                + ")";
        stmt.execute(createTable);
        createTable = "CREATE TABLE IF NOT EXISTS  moon "
                + "(name text NOT NULL,"
                + "planet_id integer NOT NULL REFERENCES planet(id) ON UPDATE CASCADE ON DELETE CASCADE "
                + ")";
        stmt.execute(createTable);
        stmt.close();
    }

    @Test
    public void testJoinPlanetMoon() throws Exception {
        System.out.println("============ParentDAO:testJoinPlanetMoon==============");
        System.setProperty(SnakeORMProperties.SHOW_DML, "true");
        database.setClasses(Planet.class, Moon.class);
        List<Map<String, Object>> joins = database.getJoinsWhere(" planet.name = ? ", "earth");
        System.out.println(joins);
        Map<String, Object> join = joins.get(0);
        Assert.assertTrue(joins.size() == 1);
        Assert.assertEquals("earth moon", join.get("moon.name"));
        Assert.assertEquals("earth", join.get("planet.name"));
        Assert.assertEquals("123", join.get("planet.sys_uid"));
    }

    @Test
    public void testJoinSystemPlanet() throws Exception {
        System.out.println("============ParentDAO:testJoinSystemPlanet==============");
        System.setProperty(SnakeORMProperties.SHOW_DML, "true");
        database.setClasses(SolarSystem.class, Planet.class);
        List<Map<String, Object>> joins = this.database.getJoinsWhere(" system.name = ? ORDER BY planet.id", "solar");
        System.out.println(joins);
        Map<String, Object> earth = joins.get(0);
        Map<String, Object> mars = joins.get(1);
        Assert.assertEquals(2, joins.size());
        Assert.assertEquals("solar", earth.get("system.name"));
        Assert.assertEquals("earth", earth.get("planet.name"));
        Assert.assertEquals("solar", mars.get("system.name"));
        Assert.assertEquals("mars", mars.get("planet.name"));
    }

    @Test
    public void testJoinSystemPlanetMoonMira() throws Exception {
        System.out.println("============ParentDAO:testJoinSystemPlanetMoonMira==============");
        System.setProperty(SnakeORMProperties.SHOW_DML, "true");
        database.setClasses(SolarSystem.class, Planet.class, Moon.class);
        List<Map<String, Object>> joins = database.getJoinsWhere(" system.name = ? ORDER BY planet.id", "mira");
        System.out.println(joins);
        Assert.assertEquals(0, joins.size());
    }

    @Test
    public void testJoinSystemPlanetMoonSolar() throws Exception {
        System.out.println("============ParentDAO:testJoinSystemPlanetMoonSolar==============");
        database.setClasses(SolarSystem.class, Planet.class, Moon.class);
        List<Map<String, Object>> joins = database.getJoinsWhere(" system.name = ? ORDER BY moon.name", "solar");
        System.out.println(joins);
        Assert.assertEquals(3, joins.size());
//      this.moonDAO.insert(phobos);
//      this.moonDAO.insert(deimos);
//      this.moonDAO.insert(moon);
        Map<String, Object> phobos = joins.get(2);
        Assert.assertEquals("phobos", phobos.get("moon.name"));
        Assert.assertEquals("mars", phobos.get("planet.name"));
        Assert.assertEquals("solar", phobos.get("system.name"));

        Map<String, Object> deimos = joins.get(0);
        Assert.assertEquals("deimos", deimos.get("moon.name"));
        Assert.assertEquals("mars", deimos.get("planet.name"));
        Assert.assertEquals("solar", deimos.get("system.name"));

        Map<String, Object> moon = joins.get(1);
        Assert.assertEquals("earth moon", moon.get("moon.name"));
        Assert.assertEquals("earth", moon.get("planet.name"));
        Assert.assertEquals("solar", moon.get("system.name"));

    }

    @Test
    public void testParseMapPlanetMoonEarth() throws Exception {
        System.out.println("============ParentDAO:testParseMapPlanetMoonEarth==============");
        //System.setProperty(SnakeORMProperties.SHOW_DML, "true");
        Class[] classes = {Planet.class, Moon.class};
        database.setClasses(classes);
        List<Map<String, Object>> joins = database.getJoinsWhere(" planet.name = ? ", "earth");
        Map<String, Object> join = joins.get(0);
        Map<Class, Object> parsedObjects = database.parseMap(join);
        System.out.println(parsedObjects);
        Planet earth = (Planet) parsedObjects.get(Planet.class);
        Assert.assertEquals("earth", earth.name);
        Assert.assertEquals("123", earth.sys_uid);

        Moon moon = (Moon) parsedObjects.get(Moon.class);
        Assert.assertEquals("earth moon", moon.name);
    }

    @Test
    public void testParseJoinsSolarSystemPlanet() throws Exception {
        System.out.println("============ParentDAO:testParseJoinsSolarSystemPlanet==============");
        database.setClasses(Planet.class, SolarSystem.class);
        database.getJoinsWhere("");
        database.parseJoins();
        Map<Class, Map> chache = database.getParsedChache();
        System.out.println(chache);

    }

    @Test
    public void testParseJoinsSolarSystemPlanetSolar() throws Exception {
        System.out.println("============ParentDAO:testParseJoinsSolarSystemPlanet==============");
        database.setClasses(Planet.class, SolarSystem.class);
        database.getJoinsWhere(" system.name = ? ", "solar");
        database.parseJoins();
        Map<Class, Map> chache = database.getParsedChache();
        SolarSystem solar = (SolarSystem) chache.get(SolarSystem.class).values().iterator().next();
        System.out.println(solar);
        Assert.assertEquals("solar", solar.name);
        Assert.assertEquals("123", solar.id.uid);
        //one-to-many
        System.out.println(solar.planets);
        List<String> planetNames = new LinkedList();
        for(Object planet:solar.planets.values()){
            Planet pl = (Planet) planet;
            planetNames.add(pl.name);
        }
        Assert.assertEquals(2,planetNames.size());
        Assert.assertTrue(planetNames.contains("earth"));
        Assert.assertTrue(planetNames.contains("mars"));
        //many-to-one
        for(Object obj:chache.get(Planet.class).values()){
            Planet pl = (Planet) obj;
            Assert.assertEquals("solar", pl.system.name);
        }
        Assert.assertEquals(2, chache.get(Planet.class).values().size());
    }

}
