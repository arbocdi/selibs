package net.sf.selibs.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import net.sf.selibs.orm.types.TypeMapping.DbType;

/**
 *
 * @author selibs
 */
public class JDBCUtils {

    public static void rollback(Connection con) {
        try {
            con.rollback();
        } catch (Exception ignored) {

        }
    }

    public static void setAutocommitTrue(Connection con) {
        try {
            con.setAutoCommit(true);
        } catch (Exception ignored) {

        }
    }

    public static void setToPreparedStatement(PreparedStatement stmt, Object... values) throws SQLException {
        List<DbType> dbTypes = new LinkedList();
        List valuesList = new LinkedList();
        for (Object val : values) {
            dbTypes.add(null);
            valuesList.add(val);
        }
        setToPreparedStatement(stmt, dbTypes, valuesList);
    }

    public static void setToPreparedStatement(PreparedStatement stmt, List<DbType> dbTypes, List values) throws SQLException {
        int index = 1;
        for (Object value : values) {
            DbType dbType = dbTypes.get(index - 1);
            if (dbType != null) {
                stmt.setObject(index, value, dbType.sqlType);
            } else {
                stmt.setObject(index, value);
            }
            index++;
        }

    }

    public static <T> T getInstanceFromResultSet(ResultSet rs, Class clazz, List<Field> fields, int startIndex) throws InstantiationException, IllegalAccessException, SQLException {
        T obj = (T) clazz.newInstance();
        int index = startIndex;
        for (Field field : fields) {
            field.set(obj, rs.getObject(index));
            index++;
        }
        return obj;
    }

//    public static <T> List<T> getListFromResultSet(ResultSet rs, Class clazz, List<Field> fields) throws SQLException, InstantiationException, IllegalAccessException {
//        List<T> objects = new LinkedList();
//        while (rs.next()) {
//            objects.add(JDBCUtils.<T>getInstanceFromResultSet(rs, clazz, fields));
//        }
//        return objects;
//    }
    public static void close(AutoCloseable ac) {
        if (ac != null) {
            try {
                ac.close();
            } catch (Exception ex) {
            }
        }
    }

    public static void setShowSQL(boolean show) {
        System.setProperty(SnakeORMProperties.SHOW_DML, String.valueOf(show));
    }

    public static boolean showSQL() {
        String showSQL = System.getProperty(SnakeORMProperties.SHOW_DML);
        if (showSQL != null && showSQL.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

}
