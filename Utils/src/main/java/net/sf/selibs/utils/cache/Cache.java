package net.sf.selibs.utils.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.sf.selibs.utils.filter.Filter;

public interface Cache<K, V> {

    void put(K k, V v);
    
    void renew(Map<K,V> data);

    V get(K k);
    
    void clear();
    
    List<V> find(Filter f);
    
    V findFirst(Filter f);
    
    Collection<V> getAll();
}
