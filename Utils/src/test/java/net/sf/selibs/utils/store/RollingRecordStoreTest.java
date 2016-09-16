/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.store;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import net.sf.selibs.utils.store.FileRecordStoreTest.UserRecord;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author root
 */
public class RollingRecordStoreTest {

    private RollingFileRecordStore<String, String> store;
    FileRecordStoreTest.UserRecord arboc;
    FileRecordStoreTest.UserRecord dh;

    public RollingRecordStoreTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        store = new RollingFileRecordStore<String, String>(new File("users.txt"), new FileRecordStoreTest.UserRecordParser());
        this.store.setMaxRecords(2);
        store.removeAll();

        arboc = new UserRecord("arb", "Arboc Digambara");
        store.addRecord(arboc);

        dh = new UserRecord("dh", "Dhurva");
        store.addRecord(dh);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAdd() throws Exception {
        System.out.println("========RollingRecordStoreTest:testAdd==========");
        UserRecord maya = new UserRecord("ma", "Maya Lightbringer");
        this.store.addRecord(maya);
        Assert.assertEquals(maya, this.store.getRecord(maya.getKey()));
        Assert.assertEquals(dh, this.store.getRecord(dh.getKey()));
        Assert.assertEquals(2, this.store.readAll().size());
    }

    @Test
    public void testAddAll() throws Exception {
        System.out.println("========RollingRecordStoreTest:testAdd==========");
        UserRecord maya = new UserRecord("ma", "Maya Lightbringer");
        UserRecord maya2 = new UserRecord("ma2", "Maya Lightbringer2");
        UserRecord maya3 = new UserRecord("ma3", "Maya Lightbringer3");

        List<Record<String, String>> records = new LinkedList();
        records.add(maya);
        records.add(maya2);
        records.add(maya3);

        this.store.addAll(records);
        Assert.assertEquals(maya2, this.store.getRecord(maya2.getKey()));
        Assert.assertEquals(maya3, this.store.getRecord(maya3.getKey()));
        Assert.assertEquals(2, this.store.readAll().size());
    }

}
