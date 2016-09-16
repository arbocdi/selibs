package net.sf.selibs.utils.graph;

import java.util.Collection;
import java.util.LinkedList;

public class DistinctList<E> extends LinkedList<E> {

    @Override
    public boolean add(E e) {
        if (this.contains(e)) {
            return false;
        } else {
            return super.add(e);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean result = true;
        for (E e : c) {
            result = result & this.add(e);
        }
        return result;
    }

    @Override
    public boolean contains(Object o) {
        for (E e : this) {
            if (e == o) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object o : this) {
            sb.append(o);
            sb.append("\n");
        }
        return sb.toString();
    }

}
