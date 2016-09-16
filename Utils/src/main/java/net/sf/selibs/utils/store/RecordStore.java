/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.store;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author root
 * @param <Record>
 */
public interface RecordStore<K,V> {

    void addRecord(Record<K,V> r) throws Exception;

    void addAll(Collection<Record<K, V>> r) throws Exception;

    List<Record<K, V>> readAll() throws Exception;

    Record<K, V> getRecord(K key) throws Exception;

    Record removeRecord(K key) throws Exception;
    
    List<Record<K,V>> removeAll(Collection<K> keys) throws Exception;

    void removeAll() throws Exception;
}
