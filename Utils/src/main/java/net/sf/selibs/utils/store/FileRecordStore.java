/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.store;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.utils.misc.UHelper;

public class FileRecordStore<K, V> implements RecordStore<K, V> {

    public static final String RECORD_SEPARATOR = "\n";

    protected File file;
    protected RecordParser<K, V> parser;

    public FileRecordStore(File file, RecordParser<K, V> parser) {
        this.file = file;
        this.parser = parser;
    }

    @Override
    public synchronized void addRecord(Record<K, V> r) throws Exception {
        BufferedOutputStream bout = null;
        try {
            bout = new BufferedOutputStream(new FileOutputStream(file, true));
            bout.write(this.parser.toString(r).getBytes("UTF-8"));
            bout.write(RECORD_SEPARATOR.getBytes("UTF-8"));
        } finally {
            UHelper.close(bout);
        }
    }

    @Override
    public synchronized void addAll(Collection<Record<K, V>> r) throws Exception {
        BufferedOutputStream bout = null;
        try {
            bout = new BufferedOutputStream(new FileOutputStream(file, true));
            for (Record<K, V> record : r) {
                bout.write(this.parser.toString(record).getBytes("UTF-8"));
                bout.write(RECORD_SEPARATOR.getBytes("UTF-8"));
            }
        } finally {
            UHelper.close(bout);
        }

    }

    @Override
    public synchronized List<Record<K, V>> readAll() throws Exception {
        BufferedReader rd = null;
        try {
            List<Record<K, V>> records = new ArrayList();
            rd = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while (true) {
                String line = rd.readLine();
                if (line == null) {
                    break;
                }
                records.add(this.parser.fromString(line));
            }
            return records;
        } finally {
            UHelper.close(rd);
        }
    }

    @Override
    public synchronized Record<K, V> getRecord(K key) throws Exception {
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while (true) {
                String line = rd.readLine();
                if (line == null) {
                    return null;
                }
                Record<K, V> record = this.parser.fromString(line);
                if (record.getKey().equals(key)) {
                    return record;
                }
            }
        } finally {
            UHelper.close(rd);
        }
    }

    @Override
    public synchronized Record<K, V> removeRecord(K key) throws Exception {
        Collection<Record<K, V>> records = this.readAll();
        List<Record<K, V>> recordsNew = new LinkedList();
        Record<K, V> record = null;
        for (Record<K, V> r : records) {
            if (r.getKey().equals(key)) {
                record = r;
                continue;
            }
            recordsNew.add(r);
        }
        this.removeAll();
        this.addAll(recordsNew);
        return record;
    }

    @Override
    public synchronized void removeAll() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(0);
        raf.close();
    }

    @Override
    public synchronized List<Record<K, V>> removeAll(Collection<K> keys) throws Exception {
        Collection<Record<K, V>> records = this.readAll();
        List<Record<K, V>> recordsNew = new LinkedList();
        List<Record<K, V>> removedRecords = new LinkedList();
        for (Record<K, V> r : records) {
            if (keys.contains(r.getKey())) {
                removedRecords.add(r);
                continue;
            }
            recordsNew.add(r);
        }
        this.removeAll();
        this.addAll(recordsNew);
        return removedRecords;
    }

}
