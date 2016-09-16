/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.store;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.utils.misc.UHelper;
import static net.sf.selibs.utils.store.FileRecordStore.RECORD_SEPARATOR;

/**
 *
 * @author root
 */
public class RollingFileRecordStore<K, V> extends FileRecordStore<K, V> {
    
    @Setter
    @Getter
    public int maxRecords = 10000;
    
    public RollingFileRecordStore(File file, RecordParser parser) {
        super(file, parser);
    }
    
    @Override
    public synchronized void addRecord(Record<K, V> r) throws Exception {
        List<Record<K, V>> records = new LinkedList();
        records.add(r);
        this.addAll(records);
    }
    
    @Override
    public synchronized void addAll(Collection<Record<K, V>> r) throws Exception {
        List<Record<K, V>> records = this.readAll();
        records.addAll(r);
        if (records.size() > this.maxRecords) {
            int startIndex = records.size() - this.maxRecords;
            this.removeAll();
            super.addAll(records.subList(startIndex, records.size()));
        }
        else{
            super.addAll(r);
        }
    }
    
}
