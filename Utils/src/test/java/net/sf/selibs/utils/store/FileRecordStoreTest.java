/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.store;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import lombok.Data;
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
public class FileRecordStoreTest {

    private FileRecordStore<String, String> store;
    UserRecord arboc;
    UserRecord dh;

    public FileRecordStoreTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        store = new FileRecordStore<String, String>(new File("users.txt"), new UserRecordParser());
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
    public void testAddGet() throws Exception {
        System.out.println("=============FileRecordStoreTest:testAddGet==================");
        Assert.assertEquals(arboc, this.store.getRecord(arboc.getKey()));
        Assert.assertEquals(dh, this.store.getRecord(dh.getKey()));

    }

    @Test
    public void testReadAll() throws Exception {
        System.out.println("=============FileRecordStoreTest:testReadAll==================");
        List<Record<String, String>> records = (List<Record<String, String>>) this.store.readAll();
        Assert.assertEquals(this.arboc, records.get(0));
        Assert.assertEquals(this.dh, records.get(1));
        Assert.assertEquals(2, records.size());
    }

    @Test
    public void testRemoveRecord() throws Exception {
        System.out.println("=============FileRecordStoreTest:testRemoveRecord==================");
        this.store.removeRecord(this.arboc.getKey());
        Assert.assertEquals(dh, this.store.getRecord(dh.getKey()));
        Assert.assertNull(this.store.getRecord(arboc.getKey()));
        List<Record<String, String>> records = (List<Record<String, String>>) this.store.readAll();
        System.out.println(records);
        Assert.assertEquals(1, records.size());
    }

    @Test
    public void testRemoveAll() throws Exception {
        System.out.println("=============FileRecordStoreTest:testRemoveAll==================");
        List<String> keys = new LinkedList();
        keys.add(this.arboc.getKey());
        keys.add("dfvfg");
        Collection removed = this.store.removeAll(keys);
        System.out.println(removed);
        removed.contains(arboc);
        Assert.assertNull(this.store.getRecord(this.arboc.getKey()));
        System.out.println(this.store.readAll());
    }

    @Data
    public static class UserRecord implements Record<String, String> {

        private String value;
        private String key;

        public UserRecord(String key, String value) {
            this.value = value;
            this.key = key;
        }

    }

    public static class UserRecordParser implements RecordParser<String, String> {

        @Override
        public String toString(Record<String, String> r) throws Exception {
            return String.format("%s#%s", r.getKey(), r.getValue());
        }

        @Override
        public Record<String, String> fromString(String str) throws Exception {
            String[] data = str.split("#");
            UserRecord record = new UserRecord(data[0], data[1]);
            return record;
        }

    }

}
