package net.sf.selibs.orm;

import net.sf.selibs.orm.JoinField;
import net.sf.selibs.orm.ParentDAO;
import net.sf.selibs.orm.properties.EntityProperties;
import net.sf.selibs.orm.Database;
import net.sf.selibs.orm.relations.Moon;
import net.sf.selibs.orm.relations.Planet;
import net.sf.selibs.orm.relations.SolarSystem;
import net.sf.selibs.orm.relations.Star;
import net.sf.selibs.orm.sql.SQLGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseTest {
    
    public Database ts;
    
    @Before
    public void before() {
        ts = new Database("net.sf.selibs.orm.relations");
    }
    
    @Test
    public void testGetInnerJoinSystemPlanet() throws Exception {
        System.out.println("============Database:testGetInnerJoinSystemPlanet================");
        String sql = this.ts.getInnerJoin(SolarSystem.class, Planet.class);
        System.out.println(sql);
        Assert.assertEquals("SELECT system.name,system.id,system.uid,planet.name,planet.sys_id,planet.sys_uid,planet.id\n"
                + "FROM system,planet\n"
                + "WHERE system.id = planet.sys_id AND system.uid = planet.sys_uid", sql);
    }
    
    @Test
    public void testGetInnerJoinSystemPlanetMoon() throws Exception {
        System.out.println("=========Database:testGetInnerJoinSystemPlanetMoon===========");
        String sql = this.ts.getInnerJoin(SolarSystem.class, Planet.class, Moon.class);
        System.out.println(sql);
        Assert.assertEquals("SELECT system.name,system.id,system.uid,planet.name,planet.sys_id,planet.sys_uid,planet.id,moon.name,moon.planet_id\n"
                + "FROM system,planet,moon\n"
                + "WHERE system.id = planet.sys_id AND system.uid = planet.sys_uid AND planet.id = moon.planet_id", sql);
    }
    
    @Test
    public void testGetInnerJoinStarSystemPlanetMoon() throws Exception {
        System.out.println("=========Database:testGetInnerJoinStarSystemPlanetMoon===========");
        String sql = this.ts.getInnerJoin(Star.class, SolarSystem.class, Planet.class, Moon.class);
        System.out.println(sql);
        Assert.assertEquals("SELECT stars.name,stars.sys_id,stars.sys_uid,stars.id,system.name,system.id,system.uid,planet.name,planet.sys_id,planet.sys_uid,planet.id,moon.name,moon.planet_id\n"
                + "FROM stars,system,planet,moon\n"
                + "WHERE system.id = stars.sys_id AND system.uid = stars.sys_uid AND system.id = planet.sys_id AND system.uid = planet.sys_uid AND planet.id = moon.planet_id", sql);
    }
}
