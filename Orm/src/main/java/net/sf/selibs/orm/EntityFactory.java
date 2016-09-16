package net.sf.selibs.orm;

import net.sf.selibs.orm.properties.ClassUtils;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.selibs.orm.properties.ColumnProperties;
import net.sf.selibs.orm.properties.EntityProperties;

/**
 * Producing entities form ResultSet.
 *
 * @author selibs
 * @param <E>
 */
public class EntityFactory<E> {

    protected Object getFromRS(Class clazz, ResultSet rs, int index) throws SQLException {
        if (rs.getObject(index) == null) {
            return null;
        }
        if (clazz == byte.class || clazz == Byte.class) {
            return rs.getByte(index);
        }
        if (clazz == short.class || clazz == Short.class) {
            return rs.getShort(index);

        }
        if (clazz == int.class || clazz == Integer.class) {
            return rs.getInt(index);

        }
        if (clazz == long.class || clazz == Long.class) {
            return rs.getLong(index);
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            return rs.getBoolean(index);
        }
        if (clazz == float.class || clazz == Float.class) {
            return rs.getFloat(index);
        }
        if (clazz == double.class || clazz == Double.class) {
            return rs.getDouble(index);
        }
        if (clazz == Timestamp.class) {
            return rs.getTimestamp(index);
        }
        if (clazz == String.class) {
            return rs.getString(index);
        }
        return rs.getObject(index);

    }

    public E produceFromRS(ResultSet rs, Class clazz, List<Field> fields, int startIndex) throws InstantiationException, IllegalAccessException, SQLException {
        E entity = (E) clazz.newInstance();
        int index = startIndex;
        for (Field field : fields) {
            field.set(entity, this.getFromRS(field.getType(), rs, index));
            index++;
        }
        return entity;
    }

    /**
     * Get entity and its PK from resultset
     *
     * @param rs
     * @param entityProperties
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public E produceFromRS(ResultSet rs, EntityProperties entityProperties) throws InstantiationException, IllegalAccessException, SQLException {
        E e = this.produceFromRS(rs,
                entityProperties.getEntityClass(),
                entityProperties.getTableColumns().readableFields,
                1);
        int fieldCount = entityProperties.getTableColumns().readableFields.size();
        if (entityProperties.getPkProperties() != null) {
            Object pk = this.produceFromRS(rs,
                    entityProperties.getPkProperties().getPkClass(),
                    entityProperties.getPkProperties().getTableColumns().readableFields,
                    1 + fieldCount);
            ClassUtils.setPK(e, pk);
        }
        return e;
    }

    public E produceFromMap(Map<String, Object> map, EntityProperties entityProperties) throws InstantiationException, IllegalAccessException, SQLException {
        E e = (E) entityProperties.getEntityClass().newInstance();
        for (ColumnProperties col : entityProperties.getTableColumns().readable) {
            String fullColumnName = entityProperties.getSqlGenerator().getTable().getTableName() + "." + col.name;
            col.field.set(e, map.get(fullColumnName));
        }
        if (entityProperties.getPkProperties() != null) {
            Object pk = entityProperties.getPkProperties().getPkClass().newInstance();
            for (ColumnProperties col : entityProperties.getPkProperties().getTableColumns().readable) {
                String fullColumnName = entityProperties.getSqlGenerator().getTable().getTableName() + "." + col.name;
                col.field.set(pk, map.get(fullColumnName));
            }
            ClassUtils.setPK(e, pk);
        }
        return e;
    }

    /**
     * Produce Map<String,Object> from ResultSet, map keys are column names
     *
     * @param rs
     * @param columns column names
     * @param columnClasses
     * @return
     * @throws SQLException
     */
    public Map<String, Object> produceMapFromRS(ResultSet rs, List<String> columns, List<Class> columnClasses) throws SQLException {
        Map<String, Object> result = new HashMap();
        for (int index = 0; index < columns.size(); index++) {
            String column = columns.get(index);
            Class clazz = columnClasses.get(index);
            result.put(column, this.getFromRS(clazz, rs, index + 1));
        }
        return result;
    }
}
