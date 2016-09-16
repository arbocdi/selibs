package net.sf.selibs.orm;

import net.sf.selibs.orm.properties.ClassUtils;
import java.lang.reflect.Field;
import net.sf.selibs.orm.properties.EntityProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.selibs.orm.db_types.DbTypesLoader;
import net.sf.selibs.orm.spec.DataAccessException;
import net.sf.selibs.orm.spec.DAOInterface;
import net.sf.selibs.orm.sql.SQLGenerator;
import net.sf.selibs.orm.sql.SQLUtils;
import net.sf.selibs.orm.types.TypeMapping.DbType;
import net.sf.selibs.orm.types.TypeMapping.DbTypes;

/**
 *
 * @author selibs
 * @param <E> entity class
 */
@Slf4j
public class ParentDAO<E> implements DAOInterface<E> {

    @Getter
    @Setter
    protected Connection con;
    @Getter
    protected EntityProperties entityProperties;
    @Setter
    @Getter
    protected boolean showSql;
    @Setter
    @Getter
    protected EntityFactory<E> entityFactory;

    public ParentDAO(Class clazz, SQLGenerator sqlGen) {
        this.entityProperties = new EntityProperties(clazz, sqlGen);
        this.entityFactory = new EntityFactory();
    }

    @Override
    public int insert(E e) throws DataAccessException {
        PreparedStatement stmt = null;
        try {
            String sql = this.entityProperties.getInsert();
            if (JDBCUtils.showSQL()) {
                log.debug("\n======DML=====\n" + sql);
            }
            stmt = con.prepareStatement(sql);
            List values = this.entityProperties.getAllInsertableFieldValues(e);
            JDBCUtils.setToPreparedStatement(stmt, values.toArray());
            return stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
        }
    }

    public void batchInsert(List<E> list) throws Exception {
        PreparedStatement stmt = null;
        try {
            String sql = this.entityProperties.getInsert();
            if (JDBCUtils.showSQL()) {
                log.debug("\n======DML=====\n" + sql);
            }
            stmt = con.prepareStatement(sql);
            for (E e : list) {
                List values = this.entityProperties.getAllInsertableFieldValues(e);
                List<DbType> types = this.entityProperties.getDbTypes(this.entityProperties.getAllInsertableFields());
                JDBCUtils.setToPreparedStatement(stmt, types, values);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } finally {
            JDBCUtils.close(stmt);
        }
    }

    public void safeBatchInsert(List<E> list,int step) throws Exception {
        PreparedStatement stmt = null;
        try {
            String sql = this.entityProperties.getInsert();
            if (JDBCUtils.showSQL()) {
                log.debug("\n======DML=====\n" + sql);
            }
            con.setAutoCommit(false);
            stmt = con.prepareStatement(sql);
            int indx = 0;
            for (E e : list) {
                List values = this.entityProperties.getAllInsertableFieldValues(e);
                List<DbType> types = this.entityProperties.getDbTypes(this.entityProperties.getAllInsertableFields());
                JDBCUtils.setToPreparedStatement(stmt, types, values);
                stmt.execute();
                if (indx % step == 0) {
                    con.commit();
                }
            }
            con.commit();
        } catch (Exception ex) {
            con.rollback();
            throw ex;
        } finally {
            JDBCUtils.close(stmt);
            con.setAutoCommit(true);
        }
    }

    @Override
    public int remove(E e) throws DataAccessException {
        PreparedStatement stmt = null;
        try {
            String sql = this.entityProperties.getRemove();
            if (JDBCUtils.showSQL()) {
                log.debug("\n======DML=====\n" + sql);
            }

            stmt = con.prepareStatement(sql);
            List values = this.entityProperties.getPkFieldValues(e);
            JDBCUtils.setToPreparedStatement(stmt, values.toArray());
            return stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
        }
    }

    /**
     *
     * INSERT INTO table_name (col1,...colN) VALUES (?,...,?) RETURNING
     * pkCol1,pkCol2...
     */
    @Override
    public void insertReturnPK(E e) throws DataAccessException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = this.entityProperties.getInsert();
            if (JDBCUtils.showSQL()) {
                log.debug("\n======DML=====\n" + sql);
            }

            List<String> pkColumnNames = new LinkedList();
            if (this.entityProperties.getPkProperties() != null) {
                pkColumnNames = this.entityProperties.getPkProperties().getTableColumns().readableNames;
            }
            stmt = con.prepareStatement(sql, pkColumnNames.toArray(new String[pkColumnNames.size()]));
            List values = this.entityProperties.getAllInsertableFieldValues(e);
            JDBCUtils.setToPreparedStatement(stmt, values.toArray());
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                Object primaryKey = JDBCUtils.getInstanceFromResultSet(rs,
                        this.entityProperties.getPkProperties().getPkClass(),
                        this.entityProperties.getPkProperties().getTableColumns().readableFields,
                        1);
                this.entityProperties.getPkField().set(e, primaryKey);
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
            JDBCUtils.close(rs);
        }
    }

    public int update(E e) throws DataAccessException {
        if (this.entityProperties.getPkProperties() == null) {
            throw new DataAccessException("Cant update entity without PK");
        }
        PreparedStatement stmt = null;
        try {
            String sql = this.entityProperties.getUpdateByPK();
            if (JDBCUtils.showSQL()) {
                log.debug("\n======DML=====\n" + sql);
            }

            stmt = con.prepareStatement(sql);
            List values = this.entityProperties.getAllUpdatableFieldValues(e);
            values.addAll(this.entityProperties.getPkFieldValues(e));
            JDBCUtils.setToPreparedStatement(stmt, values.toArray());
            return stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
        }
    }

    /**
     * UPDATE table SET columns WHERE pk1=? AND pk2=?...
     *
     * @param e entity
     * @param columns colum names as declared in class to update col1,col2...
     * @return rows affected
     */
    public int updateSpecifiedColumns(E e, String columns) throws DataAccessException {
        if (this.entityProperties.getPkProperties() == null) {
            throw new DataAccessException("Cant update entity without PK");
        }
        PreparedStatement stmt = null;
        try {
            List<String> fieldNames = SQLUtils.makeListFromComaSeparatedString(columns);
            List<Field> updatableFields = this.entityProperties.getAllUpdatableFields(fieldNames);
            List<String> columnNames = ClassUtils.getColumnNames(updatableFields);
            String set = SQLUtils.makeSetColumns(columnNames);
            String sql = this.entityProperties.getUpdateByPKSet(set);
            if (JDBCUtils.showSQL()) {
                log.debug("\n======DML=====\n" + sql);
            }
            stmt = con.prepareStatement(sql);
            List values = this.entityProperties.getAllUpdatableFieldValues(fieldNames, e);
            values.addAll(this.entityProperties.getPkFieldValues(e));
            System.out.println(values);

            JDBCUtils.setToPreparedStatement(stmt, values.toArray());
            return stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
        }
    }

    /**
     * Remove all records from the table
     *
     * @param clazz
     * @return
     * @throws net.sf.selibs.orm.spec.DataAccessException
     */
    @Override
    public int clearTable() throws DataAccessException {
        PreparedStatement stmt = null;
        String sql = this.entityProperties.getSqlGenerator().getClearTable();
        if (JDBCUtils.showSQL()) {
            log.debug("\n======DML=====\n" + sql);
        }
        try {
            stmt = con.prepareStatement(sql);
            return stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
        }
    }

    /**
     * Drop table
     *
     * @param clazz
     * @return
     */
    @Override
    public int dropTable() throws DataAccessException {
        String sql = this.entityProperties.getSqlGenerator().getDropTable();
        if (JDBCUtils.showSQL()) {
            log.debug("\n======DML=====\n" + sql);
        }
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(sql);
            return stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
        }
    }

    /**
     * Get all records from table
     *
     * @param clazz
     * @return
     * @throws net.sf.selibs.orm.spec.DataAccessException
     */
    @Override
    public List<E> findAll() throws DataAccessException {
        List<E> result = new LinkedList();
        String sql = this.entityProperties.getSelect();
        if (JDBCUtils.showSQL()) {
            log.debug("\n======DML=====\n" + sql);
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(this.entityFactory.produceFromRS(rs, this.entityProperties));
            }
            return result;
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
            JDBCUtils.close(rs);

        }
    }

    /**
     * Get record by its PK
     *
     * @param clazz
     * @param pk
     * @return
     * @throws net.sf.selibs.orm.spec.DataAccessException
     */
    @Override
    public E find(Object pk) throws DataAccessException {
        String sql = this.entityProperties.getReadByPK();
        if (JDBCUtils.showSQL()) {
            log.debug("\n======DML=====\n" + sql);
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(sql);
            List pkValues = ClassUtils.getFieldValues(this.entityProperties.getPkProperties().getTableColumns().readableFields, pk);
            JDBCUtils.setToPreparedStatement(stmt, pkValues.toArray());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return this.entityFactory.produceFromRS(rs, this.entityProperties);
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
            JDBCUtils.close(rs);

        }
    }

    /**
     * SELECT col1,col2... FROM table WHERE condition
     *
     * @param clazz
     * @param condition col1>? AND colN=? etc
     * @param values values for condition variables
     * @return
     */
    public List<E> findWhere(String condition, Object... values) throws DataAccessException {
        List<E> results = new LinkedList();
        String sql = this.entityProperties.getSelectWithCondition(condition);
        if (JDBCUtils.showSQL()) {
            log.debug("\n======DML=====\n" + sql);
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(sql);
            JDBCUtils.setToPreparedStatement(stmt, values);
            rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(this.entityFactory.produceFromRS(rs, this.entityProperties));
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
            JDBCUtils.close(rs);

        }
        return results;
    }

    /**
     * SELECT col1,col2... FROM table WHERE condition
     *
     * @param clazz
     * @param condition col1>? AND colN=? etc
     * @param values values for condition variables
     * @return
     * @throws net.sf.selibs.orm.spec.DataAccessException
     *
     */
    @Override
    public E findWhereSingle(String condition, Object... values) throws DataAccessException {
        String sql = this.entityProperties.getSelectWithCondition(condition);
        if (JDBCUtils.showSQL()) {
            log.debug("\n======DML=====\n" + sql);
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(sql);
            JDBCUtils.setToPreparedStatement(stmt, values);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return this.entityFactory.produceFromRS(rs, this.entityProperties);
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.close(stmt);
            JDBCUtils.close(rs);

        }
    }

//   
}
