package net.sf.selibs.orm.properties;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EmbeddedId;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.orm.db_types.DbTypesLoader;
import net.sf.selibs.orm.spec.DatabaseType;
import net.sf.selibs.orm.sql.SQLGenerator;
import net.sf.selibs.orm.sql.SQLUtils;
import net.sf.selibs.orm.types.TypeMapping.DbType;
import net.sf.selibs.orm.types.TypeMapping.DbTypes;

/**
 *
 * @author selibs
 */
public class EntityProperties {

    public static final String AND = " AND ";

    @Getter
    protected Class entityClass;
    @Getter
    protected Field pkField;
    @Getter
    protected PKProperties pkProperties;
    @Getter
    protected SQLGenerator sqlGenerator;
    @Getter
    protected TableColumns tableColumns;
    @Getter
    protected List<JoinsProperties> joinColumnsProperties;
    @Setter
    @Getter
    protected DbTypes dbTypes;

    public EntityProperties(Class entityClass, SQLGenerator sqlGenerator) {
        this.entityClass = entityClass;
        this.sqlGenerator = sqlGenerator;
        sqlGenerator.setTable(ClassUtils.getTable(entityClass));
        this.pkProperties = ClassUtils.getPKProperties(entityClass);
        //pkField
        this.pkField = ClassUtils.getAnnotatedField(entityClass, EmbeddedId.class);
        //tableColumns
        this.tableColumns = ClassUtils.getTableColumns(entityClass);
        //joins
        joinColumnsProperties = JoinsProperties.getFromClass(entityClass);

        dbTypes = DbTypesLoader.getPostgres();
    }

    public List<DbType> getDbTypes(List<Field> fields) {
        List<DbType> types = new LinkedList();
        for (Field f : fields) {
            DatabaseType dbType = f.getAnnotation(DatabaseType.class);
            if (dbType == null) {
                types.add(null);
            } else {
                types.add(this.dbTypes.getByDbTypeName(dbType.value()));
            }
        }
        return types;
    }

    /**
     *
     * @return INSERT INTO table (col1,col2...) VALUES(?,...,?)
     */
    public String getInsert() {
        return this.sqlGenerator.getInsert(this.getAllInsertableFieldNames());
    }

    /**
     *
     * @return INSERT INTO table (col1,col2...) VALUES(?,...,?) RETURNING
     * idCol1,idCol2...
     */
    public String getInsertReturning() {
        return this.sqlGenerator.getInsertReturning(this.getAllInsertableFieldNames(), this.pkProperties.tableColumns.readableNames);
    }

    /**
     *
     * @param set
     * @return UPDATE table SET _set_ WHERE pkCol1=? AND pkCol2=?...
     */
    public String getUpdateByPKSet(String set) {
        return this.sqlGenerator.getUpdatePKSet(this.pkProperties.tableColumns.readableNames, set);
    }

    /**
     *
     * @return UPDATE table SET col1=?,col2=?... WHERE pkCol1=? AND pkCol2=?...
     */
    public String getUpdateByPK() {
        return this.getUpdateByPKSet(SQLUtils.makeSetColumns(this.getAllUpdatableFieldNames()));
    }

    /**
     *
     * @param condition
     * @return SELECT col1,col2... FROM table WHERE condition
     */
    public String getSelectWithCondition(String condition) {
        return this.sqlGenerator.getSelectWithCondition(this.getAllReadableFieldNames(), condition);
    }

    /**
     *
     * @return SELECT col1,col2... FROM table
     */
    public String getSelect() {
        return this.sqlGenerator.getSelectFromTable(this.getAllReadableFieldNames());
    }

    /**
     *
     * @return SELECT (col1,...,colN) FROM table WHERE pkCol1=? AND pkCol2=?...
     */
    public String getReadByPK() {
        return this.sqlGenerator.getSelectWithCondition(this.getAllReadableFieldNames(), SQLUtils.makeAndCondition(this.pkProperties.tableColumns.readableNames));
    }

    /**
     *
     * @return DELETE FROM table WHERE pk1 = ? AND pk2 = ? ...
     */
    public String getRemove() {
        return this.sqlGenerator.getRemove(this.pkProperties.tableColumns.readableNames);
    }

    public List<Field> getAllUpdatableFields(List<String> names) {
        List<Field> fields = new LinkedList();
        List<ColumnProperties> updatableColums = new LinkedList();
        updatableColums.addAll(this.tableColumns.updatable);
        if (this.pkProperties != null) {
            updatableColums.addAll(this.pkProperties.tableColumns.updatable);
        }
        for (ColumnProperties cp : updatableColums) {
            for (String name : names) {
                if (name.equalsIgnoreCase(cp.name)) {
                    fields.add(cp.field);
                    break;
                }
            }
        }
        return fields;
    }

    public List getAllUpdatableFieldValues(List<String> names, Object entity) throws IllegalArgumentException, IllegalAccessException {
        List values = new LinkedList();
        Object pk = this.pkField.get(entity);
        List<Field> fields = this.getAllUpdatableFields(names);
        for (Field f : fields) {
            if (f.getDeclaringClass() == entity.getClass()) {
                values.add(f.get(entity));
            }
            if (f.getDeclaringClass() == pk.getClass()) {
                values.add(f.get(pk));
            }
            continue;
        }
        return values;
    }

    public List<Field> getAllInsertableFields() {
        List<Field> fields = new LinkedList();
        fields.addAll(this.tableColumns.insertableFields);
        if (this.pkProperties != null) {
            fields.addAll(this.pkProperties.tableColumns.insertableFields);
        }
        return fields;
    }

    public List getAllInsertableFieldNames() {
        return ClassUtils.getColumnNames(this.getAllInsertableFields());
    }

    public List<Field> getAllUpdatableFields() {
        List<Field> fields = new LinkedList();
        fields.addAll(this.tableColumns.updatableFields);
        if (this.pkProperties != null) {
            fields.addAll(this.pkProperties.tableColumns.updatableFields);
        }
        return fields;
    }

    public List getAllUpdatableFieldNames() {
        return ClassUtils.getColumnNames(this.getAllUpdatableFields());
    }

    public List<Field> getAllReadableFields() {
        List<Field> fields = new LinkedList();
        fields.addAll(this.tableColumns.readableFields);
        if (this.pkProperties != null) {
            fields.addAll(this.pkProperties.tableColumns.readableFields);
        }
        return fields;
    }

    public List getAllReadableFieldNames() {
        return ClassUtils.getColumnNames(this.getAllReadableFields());
    }

    public List getAllInsertableFieldValues(Object entity) throws IllegalArgumentException, IllegalAccessException {
        List values = ClassUtils.getFieldValues(this.tableColumns.insertableFields, entity);
        if (this.pkProperties != null) {
            Object pk = this.pkField.get(entity);
            values.addAll(ClassUtils.getFieldValues(this.pkProperties.tableColumns.insertableFields, pk));
        }
        return values;
    }

    public List getAllUpdatableFieldValues(Object entity) throws IllegalArgumentException, IllegalAccessException {
        List values = ClassUtils.getFieldValues(this.tableColumns.updatableFields, entity);
        if (this.pkProperties != null) {
            Object pk = this.pkField.get(entity);
            values.addAll(ClassUtils.getFieldValues(this.pkProperties.tableColumns.updatableFields, pk));
        }
        return values;
    }

    public List getPkFieldValues(Object entity) throws IllegalArgumentException, IllegalAccessException {
        if (this.pkProperties != null) {
            return ClassUtils.getFieldValues(this.pkProperties.tableColumns.readableFields, this.pkField.get(entity));
        } else {
            return new LinkedList();
        }
    }

    public List<String> getAllReadableColumnsFullNames() {
        return this.sqlGenerator.appendTableName(this.getAllReadableFieldNames());
    }

    public List<ColumnProperties> getJoinColumns(JoinsProperties jColsProps) {
        List<ColumnProperties> values = new LinkedList();
        for (JoinProperties jColProps : jColsProps.joinColumns) {
            values.add(this.tableColumns.getColumnProperties(jColProps.name));
        }
        return values;
    }

}
