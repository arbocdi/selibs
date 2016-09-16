package net.sf.selibs.orm;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import net.sf.selibs.orm.properties.ClassUtils;

public class DBCache {

    @Getter
    protected Map<Class, Map> parsedChache = new HashMap();

    public void makeLinks(Class[] classes, List<Map<Class, Object>> parsedRS) throws IllegalArgumentException, IllegalAccessException {
        for (Class clazz : classes) {
            for (Map<Class, Object> line : parsedRS) {
                Object entity = line.get(clazz);
                if (entity == null) {
                    continue;
                }
                //add many-to-one links
                for (Field manyToOne : ClassUtils.getAnnotatedFields(clazz, ManyToOne.class)) {
                    this.makeManyToOneLink(entity, line, manyToOne);
                }
                //add one-to-many links
                for (Field oneToMany : ClassUtils.getAnnotatedFields(clazz, OneToMany.class)) {
                    this.makeOneToManyLink(entity, line, oneToMany);
                }
                //add o0ne-to-one
                for (Field oneToOne : ClassUtils.getAnnotatedFields(clazz, OneToOne.class)) {
                    this.makeOneToOneLink(entity, line, oneToOne);
                }
            }
        }
    }

    protected Object add(Object entity) throws IllegalArgumentException, IllegalAccessException {
        Map map = parsedChache.get(entity.getClass());
        Object pk = ClassUtils.getPkValue(entity);
        if (map == null) {
            map = new HashMap();
            parsedChache.put(entity.getClass(), map);
        }
        Object presentEntity = map.get(pk);
        if (presentEntity != null) {
            return presentEntity;

        } else {
            map.put(pk, entity);
            return entity;
        }
    }

    protected void makeManyToOneLink(Object entity, Map<Class, Object> line, Field manyToOne) throws IllegalArgumentException, IllegalAccessException {
        Object referencedEntity = line.get(manyToOne.getType());
        manyToOne.set(entity, referencedEntity);
        this.add(entity);
    }

    protected void makeOneToManyLink(Object entity, Map<Class, Object> line, Field oneToMany) throws IllegalArgumentException, IllegalAccessException {
        entity = this.add(entity);
        Map many = (Map) oneToMany.get(entity);
        if (many == null) {
            many = new HashMap();
            oneToMany.set(entity, many);
        }
        Class referencedClass = oneToMany.getAnnotation(OneToMany.class).targetEntity();
        Object referencedEntity = line.get(referencedClass);
        many.put(ClassUtils.getPkValue(referencedEntity), referencedEntity);
    }

    protected void makeOneToOneLink(Object entity, Map<Class, Object> line, Field oneToOne) throws IllegalArgumentException, IllegalAccessException {
        entity = this.add(entity);
        Object referencedEntity = line.get(oneToOne.getType());
        if (referencedEntity != null) {
            oneToOne.set(entity, referencedEntity);
        }
    }

}
