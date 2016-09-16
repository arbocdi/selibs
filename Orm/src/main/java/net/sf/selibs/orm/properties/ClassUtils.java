package net.sf.selibs.orm.properties;

import net.sf.selibs.orm.properties.PKProperties;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import net.sf.selibs.orm.JoinField;
import net.sf.selibs.orm.sql.SQLTable;

/**
 *
 * @author selibs
 */
public class ClassUtils {
    
    public static List<Field> getFields(Class clazz) {
        List<Field> result = new LinkedList();
        for (Field f : clazz.getDeclaredFields()) {
            result.add(f);
        }
        return result;
    }

    public static List<Field> getAnnotatedFields(Class clazz, Class annotation) {
        List<Field> result = new LinkedList();
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(annotation)) {
                result.add(f);
            }
        }
        return result;
    }

    public static Field getAnnotatedField(Class clazz, Class annotation) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(annotation)) {
                return f;
            }
        }
        return null;
    }

    public static List<Field> getNotAnnotatedFields(Class clazz, Class annotation) {
        List<Field> result = new LinkedList();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(annotation)) {
                result.add(f);
            }
        }
        return result;
    }

    public static SQLTable getTable(Class clazz) {
        Table ann = (Table) clazz.getAnnotation(Table.class);
        if (ann == null) {
            return new SQLTable(clazz.getSimpleName());
        } else {
            return new SQLTable(ann.schema(), ann.name());
        }
    }

    /**
     * Using @EmbeddedId to get PKProperties
     *
     * @param clazz
     * @return
     */
    public static PKProperties getPKProperties(Class clazz) {
        List<Field> fields = ClassUtils.getAnnotatedFields(clazz, EmbeddedId.class);
        if (fields.isEmpty()) {
            return null;
        }
        Field idField = fields.get(0);
        Class idClazz = idField.getType();
        return new PKProperties(idClazz);
    }

    /**
     * insertable = true,@Transient not present,@EmbeddedId not
     * present,@GeneratedValue not present,not static
     *
     * @OneToMany,@ManyToOne
     *
     * @param clazz
     * @return insertable fields
     */
    public static List<Field> getInsertableFields(Class clazz) {
        List<Field> result = new LinkedList();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Transient.class)
                    && !f.isAnnotationPresent(EmbeddedId.class)
                    && !f.isAnnotationPresent(GeneratedValue.class)
                    && !f.isAnnotationPresent(OneToMany.class)
                    && !f.isAnnotationPresent(ManyToOne.class)
                    && !Modifier.isStatic(f.getModifiers())) {
                Column col = f.getAnnotation(Column.class);
                if (col != null) {
                    if (col.insertable()) {
                        result.add(f);
                    }
                } else {
                    result.add(f);
                }
            }
        }
        return result;
    }

    public static TableColumns getTableColumns(Class clazz) {
        TableColumns tc = new TableColumns();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Transient.class)
                    && !f.isAnnotationPresent(EmbeddedId.class)
                    && !f.isAnnotationPresent(OneToMany.class)
                    && !f.isAnnotationPresent(ManyToOne.class)
                    && !f.isAnnotationPresent(JoinColumn.class)
                    && !f.isAnnotationPresent(JoinColumns.class)
                    && !f.isAnnotationPresent(OneToOne.class)
                    && !Modifier.isStatic(f.getModifiers())) {
                ColumnProperties cp = new ColumnProperties();
                cp.field = f;
                cp.type = f.getType();
                cp.name = f.getName();
                cp.insertable = true;
                cp.updatable = true;
                Column col = f.getAnnotation(Column.class);
                if (col != null) {
                    if (!col.name().isEmpty()) {
                        cp.name = col.name();
                    }
                    if (!col.insertable()) {
                        cp.insertable = false;
                    }
                    if (!col.updatable()) {
                        cp.updatable = false;
                    }
                }
                if (f.isAnnotationPresent(GeneratedValue.class)) {
                    cp.generatedValue = true;
                    cp.insertable = false;
                    cp.updatable = false;
                }
                tc.readable.add(cp);
                tc.readableNames.add(cp.name);
                tc.readableFields.add(f);
                if (cp.insertable) {
                    tc.insertable.add(cp);
                    tc.insertableNames.add(cp.name);
                    tc.insertableFields.add(f);
                }
                if (cp.updatable) {
                    tc.updatable.add(cp);
                    tc.updatableNames.add(cp.name);
                    tc.updatableFields.add(f);
                }

            }
        }
        return tc;
    }

    /**
     * updatable = true,@Transient not present,@EmbeddedId not
     * present,@GeneratedValue not present,not static
     *
     * @OneToMany,@ManyToOne
     * @param clazz
     * @return insertable fields
     */
    public static List<Field> getUpdatableFields(Class clazz) {
        List<Field> result = new LinkedList();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Transient.class)
                    && !f.isAnnotationPresent(EmbeddedId.class)
                    && !f.isAnnotationPresent(GeneratedValue.class)
                    && !f.isAnnotationPresent(OneToMany.class)
                    && !f.isAnnotationPresent(ManyToOne.class)
                    && !Modifier.isStatic(f.getModifiers())) {
                Column col = f.getAnnotation(Column.class);
                if (col != null) {
                    if (col.updatable()) {
                        result.add(f);
                    }
                } else {
                    result.add(f);
                }
            }
        }
        return result;
    }

    /**
     * @Transient not present present,@GeneratedValue not present
     * @OneToMany,@ManyToOne,not static
     * @param clazz
     * @return insertable fields
     */
    public static List<Field> getReadableFields(Class clazz) {
        List<Field> result = new LinkedList();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(Transient.class)
                    && !f.isAnnotationPresent(EmbeddedId.class)
                    && !f.isAnnotationPresent(OneToMany.class)
                    && !f.isAnnotationPresent(ManyToOne.class)
                    && !Modifier.isStatic(f.getModifiers())) {
                result.add(f);
            }
        }
        return result;
    }

    /**
     *
     * @param fields
     * @return field names taking into account column annotation
     */
    public static List<String> getColumnNames(List<Field> fields) {
        List<String> names = new LinkedList();
        for (Field field : fields) {
            Column col = field.getAnnotation(Column.class);
            if (col != null && !col.name().isEmpty()) {
                names.add(col.name());
            } else {
                names.add(field.getName());
            }
        }
        return names;
    }

    public static List getFieldValues(List<Field> fields, Object obj) throws IllegalArgumentException, IllegalAccessException {
        List values = new LinkedList();
        for (Field field : fields) {
            field.setAccessible(true);
            values.add(field.get(obj));
        }
        return values;
    }

    public static void setPK(Object entity, Object pk) throws IllegalArgumentException, IllegalAccessException {
        Field embeddedPK = ClassUtils.getAnnotatedFields(entity.getClass(), EmbeddedId.class).get(0);
        embeddedPK.set(entity, pk);
    }

    public static List<JoinField> getJoinFields(Class clazz) {
        List<JoinField> jfs = new LinkedList();
        for (Field f : clazz.getDeclaredFields()) {
            JoinField jf = f.getAnnotation(JoinField.class);
            if (jf != null) {
                jfs.add(jf);
            }
        }
        return jfs;
    }

    public static Object getPkValue(Object entity) throws IllegalArgumentException, IllegalAccessException {
        Field pkField = ClassUtils.getAnnotatedField(entity.getClass(), EmbeddedId.class);
        return pkField.get(entity);
    }
    public static String getColumnName(Field field){
        String fieldName = field.getName();
        Column column = field.getAnnotation(Column.class);
        if(column!=null&&!column.name().isEmpty()){
            fieldName =column.name();
        }
        return fieldName;
    }

}
