package net.sf.selibs.orm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.selibs.orm.relations.Planet;
import net.sf.selibs.orm.relations.PlanetID;
import net.sf.selibs.orm.relations.SolarSystem;
import net.sf.selibs.orm.relations.Star;
import net.sf.selibs.orm.relations.StarID;
import net.sf.selibs.orm.relations.SystemID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author root
 */
public class DBChacheTest {

    List<Map<Class, Object>> data;
    SolarSystem solar;
    SolarSystem mira;
    Star sun;
    Planet earth;
    Planet mars;
    Planet naboo;

    public DBChacheTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        data = new LinkedList();
        
        solar = new SolarSystem();
        solar.name = "solar";
        solar.id = new SystemID();
        solar.id.id = 1;
        solar.id.uid = "123";
        
        sun = new Star();
        sun.id = new StarID();
        sun.id.id=1;
        sun.s_id = solar.id.id;
        sun.s_uid=solar.id.uid;

        mira = new SolarSystem();
        mira.name = "mira";
        mira.id = new SystemID();
        mira.id.id = 2;
        mira.id.uid = "123";

        earth = new Planet();
        earth.name = "earth";
        earth.sys_id = 1;
        earth.sys_uid = "123";
        earth.id = new PlanetID();
        earth.id.id = 1;

        mars = new Planet();
        mars.name = "mars";
        mars.sys_id = 1;
        mars.sys_uid = "123";
        mars.id = new PlanetID();
        mars.id.id = 2;

        naboo = new Planet();
        naboo.name = "naboo";
        naboo.sys_id = 2;
        naboo.sys_uid = "123";
        naboo.id = new PlanetID();
        naboo.id.id = 3;

        Map<Class, Object> line1 = new HashMap();
        line1.put(SolarSystem.class, solar);
        line1.put(Planet.class, earth);
        line1.put(Star.class, sun);

        Map<Class, Object> line2 = new HashMap();
        line2.put(SolarSystem.class, solar);
        line2.put(Planet.class, mars);
        //line2.put(Star.class, sun);

        Map<Class, Object> line3 = new HashMap();
        line3.put(SolarSystem.class, mira);
        line3.put(Planet.class, naboo);

        data.add(line3);
        data.add(line1);
        data.add(line2);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of makeLinks method, of class DBCache.
     */
    @Test
    public void testMakeLinks() throws Exception {
        System.out.println("=============DBChache:makeLinks==============");
        DBCache chache = new DBCache();
        Class[] classes = {Star.class,SolarSystem.class, Planet.class};
        chache.makeLinks(classes, data);
        System.out.println(chache.getParsedChache());

        //oneToMany
        SolarSystem solar1 = (SolarSystem) chache.getParsedChache().get(SolarSystem.class).get(solar.id);
        System.out.println(solar1);
        System.out.println(solar1.planets);
        Assert.assertEquals(solar, solar1);

        Planet earth1 = (Planet) solar1.planets.get(earth.id);
        Assert.assertEquals(earth, earth1);

        Planet mars1 = (Planet) solar1.planets.get(mars.id);
        Assert.assertEquals(mars, mars1);

        SolarSystem mira1 = (SolarSystem) chache.getParsedChache().get(SolarSystem.class).get(mira.id);
        System.out.println(mira1);

        Planet naboo1 = (Planet) mira1.planets.get(naboo.id);
        Assert.assertEquals(naboo, naboo1);
        //manyToOne
        Assert.assertEquals(solar, earth1.system);
        Assert.assertEquals(solar, mars1.system);
        Assert.assertEquals(mira, naboo1.system);
        //oneToOme
        Star sun1=solar1.star;
        Assert.assertEquals(sun, sun1);
        Assert.assertEquals(solar, sun1.system);
        Assert.assertNull(mira1.star);
        
    }

    /**
     * Test of add method, of class DBCache.
     *
     */
    @Test
    public void testAdd() throws Exception {
    }

    /**
     * Test of makeManyToOneLink method, of class DBCache.
     */
    @Test
    public void testMakeManyToOneLink() throws Exception {
    }

    /**
     * Test of makeOneToManyLink method, of class DBCache.
     */
    @Test
    public void testMakeOneToManyLink() throws Exception {
    }

    /**
     * Test of getParsedChache method, of class DBCache.
     */
    @Test
    public void testGetParsedChache() {
    }

}
