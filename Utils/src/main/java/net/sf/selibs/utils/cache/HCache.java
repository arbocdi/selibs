package net.sf.selibs.utils.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.selibs.utils.filter.Filter;
import net.sf.selibs.utils.filter.FilterUtils;


public class HCache<K,V> implements Cache<K,V> {
    
    protected Map<K,V> data = new HashMap();

    @Override
    public synchronized void put(K k, V v) {
        data.put(k, v);
    }

    @Override
    public synchronized V get(K k) {
        return data.get(k);
    }

    @Override
    public synchronized void clear() {
        data.clear();
    }

    @Override
    public synchronized void renew(Map<K, V> data) {
        this.data.clear();
        this.data.putAll(data);
    }

    @Override
    public synchronized List<V> find(Filter f) {
        return FilterUtils.applyFilter(data.values(), f);
    }

    @Override
    public synchronized V findFirst(Filter f) {
        return (V) FilterUtils.findFirst(data.values(), f);
    }

    @Override
    public synchronized List<V> getAll() {
        List<V> output = new LinkedList();
        for(V v:this.data.values()){
            output.add(v);
        }
        return output;
    }

    
    
    
}
