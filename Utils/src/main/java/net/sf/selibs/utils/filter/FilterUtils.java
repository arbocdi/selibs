package net.sf.selibs.utils.filter;

import java.util.LinkedList;
import java.util.List;

public class FilterUtils {

    public static <T> List<T> applyFilter(Iterable<T> elements, Filter<T> filter) {
        List<T> filtered = new LinkedList();
        for (T element : elements) {
            if (filter.applicable(element)) {
                filtered.add(element);
            }
        }
        return filtered;
    }

    public static <T> T findFirst(Iterable<T> elements, Filter<T> filter) {
        for (T element : elements) {
            if (filter.applicable(element)) {
                return element;
            }
        }
        return null;
    }
    public static <T> int findFirstIndex(Iterable<T> elements, Filter<T> filter) {
        int index=0;
        for (T element : elements) {
            if (filter.applicable(element)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
