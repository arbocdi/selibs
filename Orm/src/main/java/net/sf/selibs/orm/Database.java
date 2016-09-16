package net.sf.selibs.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import static net.sf.selibs.orm.properties.EntityProperties.AND;
import net.sf.selibs.orm.properties.JoinProperties;
import net.sf.selibs.orm.properties.JoinsProperties;
import net.sf.selibs.orm.spec.DAOCollectionInterface;
import net.sf.selibs.orm.spec.DataAccessException;
import net.sf.selibs.orm.sql.DDLGenerator;
import net.sf.selibs.orm.sql.SQLGenerator;
import net.sf.selibs.orm.sql.SQLTable;
import net.sf.selibs.orm.sql.SQLUtils;
import org.reflections.Reflections;

@Slf4j
public class Database<E> implements DAOCollectionInterface<E> {

    @Getter
    protected Map<Class, ParentDAO> tables = new HashMap<Class, ParentDAO>();
    @Getter
    protected Connection connection;
    @Getter
    @Setter
    protected EntityFactory entityFactory;
    @Getter
    protected Class[] classes;
    @Setter
    @Getter
    protected List<Map<String, Object>> joinData;
    @Setter
    @Getter
    protected DBCache dbChache;
    @Setter
    @Getter
    protected DDLGenerator ddlGenerator;

    public void setClasses(Class... classes) {
        this.classes = classes;
    }

    public Database(String... packages) {
        this.entityFactory = new EntityFactory();
        this.ddlGenerator = new DDLGenerator();
        for (String pkg : packages) {
            Reflections reflections = new Reflections(pkg);
            Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
            for (Class entity : entities) {
                tables.put(entity, new ParentDAO(entity, new SQLGenerator()));
            }
        }
    }

    public void setConnection(Connection con) {
        this.connection = con;
        for (ParentDAO dao : tables.values()) {
            dao.setCon(con);
        }
    }

    public void addDAO(ParentDAO dao) {
        this.tables.put(dao.getEntityProperties().getEntityClass(), dao);
    }

    protected List<ParentDAO> getDAOs(Class... entities) {
        List<ParentDAO> daos = new LinkedList();
        for (Class entity : entities) {
            daos.add(tables.get(entity));
        }
        return daos;
    }

    /**
     *
     * @param daos
     * @return t1.col1 t1.col2 ... tN.colM
     */
    protected List<String> getReadableColumnsNames(List<ParentDAO> daos) {
        List<String> fullNames = new LinkedList();
        for (ParentDAO dao : daos) {
            fullNames.addAll(dao.getEntityProperties().getAllReadableColumnsFullNames());
        }
        return fullNames;
    }

    /**
     *
     * @param daos
     * @return classes of all columns to read
     */
    protected List<Class> getReadableColumnsClasses(List<ParentDAO> daos) {
        List<Class> classes = new LinkedList();
        for (ParentDAO dao : daos) {
            for (Field f : dao.getEntityProperties().getAllReadableFields()) {
                classes.add(f.getType());
            }
        }
        return classes;
    }

    /**
     * appends: t1,t2,...
     *
     * @param sb
     * @param daos
     */
    protected void appendTableNames(StringBuilder sb, List<ParentDAO> daos) {
        int index = 1;
        for (ParentDAO dao : daos) {
            sb.append(dao.getEntityProperties().getSqlGenerator().getTable().getTableName());
            if (index != daos.size()) {
                sb.append(",");
            }
            index++;
        }
    }

    /**
     * appends: t1.colx = t2.coly AND t2.colz=t3.colm ...
     *
     * @param sb
     * @param daos
     */
    protected void appendJoinCondition(StringBuilder sb, List<ParentDAO> daos) {
        for (ParentDAO dao : daos) {
            List<JoinsProperties> joinColPropsList = dao.getEntityProperties().getJoinColumnsProperties();
            //List<JoinField> jfs = ClassUtils.getJoinFields(dao.getEntityProperties().getEntityClass());
            for (JoinsProperties jcProps : joinColPropsList) {
                if (this.isJoinIncluded(daos, jcProps.clazz)) {
                    SQLTable refTable = dao.getEntityProperties().getSqlGenerator().getTable();
                    SQLTable primTable = this.tables.get(jcProps.clazz).getEntityProperties().getSqlGenerator().getTable();
                    for (JoinProperties jcProp : jcProps.joinColumns) {
                        sb.append(SQLUtils.makeJoinCondition(jcProp.refColName, primTable, jcProp.name, refTable));
                        sb.append(" AND ");
                    }
                }
            }
        }
    }

    protected boolean isJoinIncluded(List<ParentDAO> daos, Class entity) {
        for (ParentDAO dao : daos) {
            if (dao.getEntityProperties().getEntityClass() == entity) {
                return true;
            }
        }
        return false;
    }

    /**
     * SELECT t1.col1,t1.col2,...,tN.col1,tN.col2... FROM t1,...,tN WHERE
     * t1.joinCol=t2.joinCol ... AND t1.joinCol=tN.joinCol AND _condition_
     *
     * @param entities
     * @return
     */
    protected String getInnerJoin(Class... entities) {
        List<ParentDAO> daos = this.getDAOs(entities);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(SQLUtils.makeComaSeparated(this.getReadableColumnsNames(daos)));
        sb.append("\nFROM ");
        this.appendTableNames(sb, daos);
        sb.append("\nWHERE ");
        this.appendJoinCondition(sb, daos);
        if (sb.toString().endsWith(AND)) {
            sb.replace(sb.length() - AND.length(), sb.length(), "");
        }
        return sb.toString();
    }

    /**
     * @param entities
     * @param condition
     * @param values
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> getJoinsWhere(String condition, Object... values) throws SQLException {
        String sql = this.getInnerJoin(classes);
        if (condition != null && !condition.isEmpty()) {
            sql += " AND " + condition;
        }
        if (JDBCUtils.showSQL()) {
            log.debug("\n======DML=====\n" + sql);
        }
        List<Map<String, Object>> results = new LinkedList();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ParentDAO> daos = this.getDAOs(classes);
        List<Class> classes = this.getReadableColumnsClasses(daos);
        List<String> columns = this.getReadableColumnsNames(daos);
        try {
            stmt = connection.prepareStatement(sql);
            JDBCUtils.setToPreparedStatement(stmt, values);
            rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(this.entityFactory.produceMapFromRS(rs, columns, classes));
            }
            this.joinData = results;
            return results;
        } finally {
            JDBCUtils.close(stmt);
            JDBCUtils.close(rs);

        }
    }

    public Map<Class, Object> parseMap(Map<String, Object> joins) throws DataAccessException {
        try {
            Map<Class, Object> map = new HashMap();
            for (Class clazz : classes) {
                ParentDAO dao = this.tables.get(clazz);
                if (dao != null) {
                    Object instance = dao.getEntityFactory().produceFromMap(joins, dao.getEntityProperties());
                    map.put(clazz, instance);
                }
            }
            return map;
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        }
    }

    public Map<Class, Map> parseJoins() throws DataAccessException {
        this.dbChache = new DBCache();
        try {
            List<Map<Class, Object>> list = new LinkedList();
            for (Map<String, Object> line : joinData) {
                list.add(this.parseMap(line));
            }
            this.dbChache.makeLinks(classes, list);
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        }
        return this.dbChache.getParsedChache();
    }

    public Map<Class, Map> getParsedChache() {
        return this.dbChache.getParsedChache();
    }

    public long clearTables() throws DataAccessException {
        long recordsRemoved = 0;
        for (ParentDAO dao : this.tables.values()) {
            recordsRemoved += dao.clearTable();
        }
        return recordsRemoved;
    }

    @Override
    public int insert(Object e) throws DataAccessException {
        ParentDAO dao = this.tables.get(e.getClass());
        return dao.insert(e);
    }

    @Override
    public int remove(Object e) throws DataAccessException {
        ParentDAO dao = this.tables.get(e.getClass());
        return dao.remove(e);

    }

    @Override
    public void insertReturnPK(Object e) throws DataAccessException {
        ParentDAO dao = this.tables.get(e.getClass());
        dao.insertReturnPK(e);
    }

    @Override
    public int update(Object e) throws DataAccessException {
        ParentDAO dao = this.tables.get(e.getClass());
        return dao.update(e);
    }

    @Override
    public int updateSpecifiedColumns(Object e, String columns) throws DataAccessException {
        ParentDAO dao = this.tables.get(e.getClass());
        return dao.updateSpecifiedColumns(e, columns);
    }

    @Override
    public int clearTable(Class clazz) throws DataAccessException {
        ParentDAO dao = this.tables.get(clazz);
        return dao.clearTable();
    }

    @Override
    public int dropTable(Class clazz) throws DataAccessException {
        ParentDAO dao = this.tables.get(clazz);
        return dao.dropTable();
    }

    public int dropTables() throws DataAccessException {
        int tablesRemoved = 0;
        for (ParentDAO dao : this.tables.values()) {
            tablesRemoved += dao.dropTable();
        }
        return tablesRemoved;
    }

    public int dropTablesNoException() {
        int tablesRemoved = 0;
        for (ParentDAO dao : this.tables.values()) {
            try {
                tablesRemoved += dao.dropTable();

            } catch (Exception ex) {
                log.warn("Cant drop table " + dao.getEntityProperties().getSqlGenerator().getTable().getTableName(), ex);
            }
        }
        return tablesRemoved;

    }

    @Override
    public <E> List<E> findAll(Class<E> clazz) throws DataAccessException {
        ParentDAO dao = this.tables.get(clazz);
        return dao.findAll();
    }

    @Override
    public <E> E find(Class<E> clazz, Object pk) throws DataAccessException {
        ParentDAO dao = this.tables.get(clazz);
        return (E) dao.find(pk);
    }

    @Override
    public <E> List<E> findWhere(Class<E> clazz, String condition, Object... values) throws DataAccessException {
        ParentDAO dao = this.tables.get(clazz);
        return dao.findWhere(condition, values);

    }

    @Override
    public <E> E findWhereSingle(Class<E> clazz, String condition, Object... values) throws DataAccessException {
        ParentDAO dao = this.tables.get(clazz);
        return (E) dao.findWhereSingle(condition, values);
    }

    public int createTables() throws DataAccessException {
        int tablesCreated = 0;
        try {
            this.connection.setAutoCommit(false);
            for (Class entity : this.classes) {
                ParentDAO dao = this.tables.get(entity);
                String sql = this.ddlGenerator.makeCreateTable(dao.getEntityProperties(), tables);
                if (JDBCUtils.showSQL()) {
                    log.debug("\n======DML=====\n" + sql);
                }
                PreparedStatement stmt = null;
                try {
                    stmt = this.connection.prepareStatement(sql);
                    stmt.executeUpdate();
                    tablesCreated++;
                } finally {
                    stmt.close();
                }
            }
            this.connection.commit();
        } catch (Exception ex) {
            JDBCUtils.rollback(connection);
            throw new DataAccessException(ex);
        } finally {
            JDBCUtils.setAutocommitTrue(connection);
        }
        return tablesCreated;
    }

}
