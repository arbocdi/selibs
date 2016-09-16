package com.megacom.ashevelev.utils.assosiation;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Набор схожих элементов.
 *
 * @author ashevelev
 */
@Root
public class Group<K extends GElement> {

    /**
     * Отличительный признак ассоциации.
     */
    @Element
    public Object groupSign;
    /**
     * Набор схожих элементов.
     */
    @ElementList
    public List<K> elements = new LinkedList();

    public Group(Object groupSign) {
        this.groupSign = groupSign;
    }

    public void addElement(K k) {
        if (k.getGroupSign().equals(groupSign)) {
            this.elements.add(k);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.groupSign);
        return hash;
    }

    /**
     * Ассоциации с одинаковым значением характерного признака считаем равными.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Group other = (Group) obj;
        return Objects.equals(this.groupSign, other.groupSign);
    }

    @Override
    public String toString() {
        try {
            StringWriter sw = new StringWriter();
            Serializer persister = new Persister();
            persister.write(this, sw);
            return sw.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.toString();

    }

    public static <K extends GElement> Map<Object, Group<K>> makeGroups(List<K> elements) {
        Map<Object, Group<K>> groups = new HashMap();
        for (GElement e : elements) {
            Object sign = e.getGroupSign();
            Group group = groups.get(sign);
            if (group == null) {
                group = new Group(sign);
                groups.put(sign, group);
            }
            group.addElement(e);
        }
        return groups;
    }
}
