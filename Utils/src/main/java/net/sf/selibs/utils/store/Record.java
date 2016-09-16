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
public interface Record<K,V> {

    void setValue(V v);

    void setKey(K k);

    V getValue();

    K getKey();
}
