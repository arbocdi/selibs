/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.store;

/**
 *
 * @author root
 */
public interface RecordParser<K,V>  {

    String toString(Record<K,V> r) throws Exception;

    Record<K,V> fromString(String str) throws Exception;
}
