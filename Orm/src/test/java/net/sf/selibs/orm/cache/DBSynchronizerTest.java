/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.cache;

import java.io.IOException;
import java.util.Collection;
import net.sf.selibs.orm.Connector;
import net.sf.selibs.orm.Database;
import net.sf.selibs.orm.JDBCUtils;
import net.sf.selibs.orm.ParentDAO;
import net.sf.selibs.orm.relations.DatabaseTest2;
import net.sf.selibs.orm.spec.DataAccessException;
import net.sf.selibs.orm.types.PostgresTypeConverter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
public class DBSynchronizerTest {

    DBSynchronizer sync;
    Database db;
    Ship abaddon;
    Ship thorax;
    Character dh;

    public DBSynchronizerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DatabaseTest2.setUpClass();
        //JDBCUtils.setShowSQL(true);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        db = new Database("net.sf.selibs.orm.cache");
        db.setConnection(Connector.getConnection());
        db.getDdlGenerator().setTypeConverter(new PostgresTypeConverter());
        db.setClasses(Ship.class, Character.class);
        db.createTables();

        abaddon = new Ship();
        abaddon.id = new ShipID();
        abaddon.id.id = "abd";
        abaddon.name = "abaddon";

        db.insert(abaddon);

        thorax = new Ship();
        thorax.id = new ShipID();
        thorax.id.id = "tx";
        thorax.name = "thorax";

        db.insert(thorax);

        dh = new Character();
        dh.id = new CharacterID();
        dh.id.id = "dh";
        dh.name = "dhurva";

        db.insert(dh);

        sync = new DBSynchronizer();
        sync.setDb(db);
        sync.setDs(Connector.getDS());

        db.setConnection(null);
    }

    @After
    public void tearDown() throws Exception {
        db.setConnection(Connector.getConnection());
        for (Object o : db.getTables().values()) {
            try {
                ((ParentDAO) o).dropTable();
            } catch (Exception ex) {
                ex.printStackTrace();
            };
        }
    }

    /**
     * Test of doStuff method, of class DBSynchronizer.
     */
    @Test
    public void testDoStuffShip() throws Exception {
        System.out.println("=============DBSync:testDoStuffShip===============");
        sync.setEntities(Ship.class);
        sync.setSleepInterval(100);
        sync.start();
        Thread.sleep(200);

        Collection values = sync.getCache().getAll();
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(2, sync.getCache().get(Ship.class).getAll().size());

        Ship abaddonDB = (Ship) sync.getCache().get(Ship.class).get(abaddon.id);
        Ship thoraxDB = (Ship) sync.getCache().get(Ship.class).get(thorax.id);
        Assert.assertEquals(abaddon, abaddonDB);
        Assert.assertEquals(thorax, thoraxDB);

        sync.stop();
        sync.getWorker().join();
    }

    @Test
    public void testDoStuffAll() throws Exception {
        System.out.println("=============DBSync:testDoStuffAll===============");
        sync.setEntities(Ship.class, Character.class);
        sync.setSleepInterval(100);
        sync.start();
        Thread.sleep(200);

        Collection values = sync.getCache().getAll();
        Assert.assertEquals(2, values.size());

        Assert.assertEquals(2, sync.getCache().get(Ship.class).getAll().size());
        Ship abaddonDB = (Ship) sync.getCache().get(Ship.class).get(abaddon.id);
        Ship thoraxDB = (Ship) sync.getCache().get(Ship.class).get(thorax.id);
        Assert.assertEquals(abaddon, abaddonDB);
        Assert.assertEquals(thorax, thoraxDB);

        Character dhDB = (Character) sync.getCache().get(Character.class).get(dh.id);
        Assert.assertEquals(dh, dhDB);
        Assert.assertEquals(1, sync.getCache().get(Character.class).getAll().size());

        sync.stop();
        sync.getWorker().join();
    }

}
