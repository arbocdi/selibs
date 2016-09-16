package net.sf.selibs.orm.types;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.ToString;
import net.sf.selibs.utils.filter.Filter;
import net.sf.selibs.utils.filter.FilterUtils;
import net.sf.selibs.utils.misc.UHelper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

@Root
// class java.sql.Types. contains generic sql(JDBC) types, common to all DBs.
// sql.Type many-to-many
// java type sql.Type one-to-one database type
public class TypeMapping {

    @ElementMap
    /**
     * java one-to-many database specific type
     */
    protected Map<String, DBTypes> javaToDB;
    @ElementMap
    /**
     * database one-to-one java type
     */
    protected Map<String, String> dbToJava;
    @Element(name = "dbName")
    protected String dbName;

    public TypeMapping(@Element(name = "dbName") String dbName) {
        javaToDB = new HashMap<String, DBTypes>();
        dbToJava = new HashMap<String, String>();
        this.dbName = dbName;
    }

    public void addMapping(String java, String db) {
        dbToJava.put(db, java);
        DBTypes dbTypes = javaToDB.get(java);
        if (dbTypes == null) {
            dbTypes = new DBTypes();
            javaToDB.put(java, dbTypes);
        }
        dbTypes.dbTypes.add(db);
    }

    @Root
    public static class DBTypes {

        @ElementList
        public Set<String> dbTypes = new HashSet();
    }

    @Root
    @ToString
    /**
     * One to one mapping between sql(jdbc) type and database specific type.
     */
    public static class DbType {

        @Element
        public String dbType;
        @Element
        public int sqlType;
        @Element(required = false)
        public String sqlTypeName;
        @Element(required = false)
        public String createParams;
        @Element
        public boolean nullable;
        @Element
        public boolean caseSensistive;
        @ElementList
        public List<String> javaTypes;
    }

    @Root
    public static class DbTypes {

        @ElementList
        public List<DbType> types;

        public DbType getByDbTypeName(final String name) {
            return FilterUtils.findFirst(types, new Filter<DbType>() {

                @Override
                public boolean applicable(DbType t) {
                    return name.equals(t.dbType);
                }
            });
        }
    }

    public static Map<Integer, String> getSqlTypeNames() throws IllegalArgumentException, IllegalAccessException {

        Map<Integer, String> result = new HashMap<Integer, String>();

        for (Field field : Types.class.getFields()) {
            result.put((Integer) field.get(null), field.getName());
        }

        return result;
    }

    public static List<DbType> getTypeMapping(Connection con) throws Exception {
        Map<Integer, String> typeNames = getSqlTypeNames();
        List<DbType> dbTypes = new LinkedList();
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            String dbName = dbmd.getDatabaseProductName();
            rs = dbmd.getTypeInfo();
            while (rs.next()) {
                DbType dbType = new DbType();
                dbType.dbType = rs.getString("TYPE_NAME");
                dbType.sqlType = rs.getShort("DATA_TYPE");
                dbType.sqlTypeName = typeNames.get(dbType.sqlType);
                dbType.createParams = rs.getString("CREATE_PARAMS");
                dbType.nullable = rs.getBoolean("NULLABLE");
                dbType.caseSensistive = rs.getBoolean("CASE_SENSITIVE");
                List<Class> javaClasses = JDBCTypeMapping.getJavaTypes(dbType.sqlType);
                dbType.javaTypes = new LinkedList();
                for (Class clazz : javaClasses) {
                    dbType.javaTypes.add(clazz.getName());
                }
                dbTypes.add(dbType);
            }
            return dbTypes;
        } finally {
            UHelper.close(rs);
        }
    }

}
