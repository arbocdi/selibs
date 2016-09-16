package net.sf.selibs.orm.types;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JDBCTypeMapping {

    public static final Map<Class, List<Integer>> javaTOjdbc = generateJavaToJDBC();

    //=================*-  
    public static List<Class> getJavaTypes(int sqlType) {
        List<Class> javaTypes = new LinkedList();
        for (Map.Entry<Class, List<Integer>> entry : javaTOjdbc.entrySet()) {
            for (int sql : entry.getValue()) {
                if (sql == sqlType) {
                    javaTypes.add(entry.getKey());
                }
            }
        }
        return javaTypes;
    }

    public static Map<Class, List<Integer>> generateJavaToJDBC() {
        Map<Class, List<Integer>> types = new HashMap();
        //String
        List<Integer> sql = new LinkedList();
        sql.add(Types.CHAR);
        sql.add(Types.VARCHAR);
        sql.add(Types.LONGVARCHAR);
        types.put(String.class, sql);
        //BigDecimal
        sql = new LinkedList();
        sql.add(Types.DECIMAL);
        sql.add(Types.NUMERIC);
        types.put(BigDecimal.class, sql);
        //Boolean
        sql = new LinkedList();
        sql.add(Types.BIT);
        types.put(Boolean.class, sql);
        //boolean
        sql = new LinkedList();
        sql.add(Types.BIT);
        types.put(boolean.class, sql);
        //Integer
        sql = new LinkedList();
        sql.add(Types.TINYINT);
        sql.add(Types.SMALLINT);
        sql.add(Types.INTEGER);
        types.put(Integer.class, sql);
        //int
        sql = new LinkedList();
        sql.add(Types.TINYINT);
        sql.add(Types.SMALLINT);
        sql.add(Types.INTEGER);
        types.put(int.class, sql);
        //Long
        sql = new LinkedList();
        sql.add(Types.BIGINT);
        types.put(Long.class, sql);
        //long
        sql = new LinkedList();
        sql.add(Types.BIGINT);
        types.put(long.class, sql);
        //Float
        sql = new LinkedList();
        sql.add(Types.REAL);
        types.put(Float.class, sql);
        //float
        sql = new LinkedList();
        sql.add(Types.REAL);
        types.put(float.class, sql);
        //Double
        sql = new LinkedList();
        sql.add(Types.DOUBLE);
        types.put(Double.class, sql);
        //double
        sql = new LinkedList();
        sql.add(Types.DOUBLE);
        types.put(double.class, sql);
        //byte[]
        sql = new LinkedList();
        sql.add(Types.BINARY);
        sql.add(Types.VARBINARY);
        sql.add(Types.LONGVARBINARY);
        types.put(byte[].class, sql);
        //Date
        sql = new LinkedList();
        sql.add(Types.DATE);
        types.put(Date.class, sql);
        //Time
        sql = new LinkedList();
        sql.add(Types.TIME);
        types.put(Time.class, sql);
        //Timestamp
        sql = new LinkedList();
        sql.add(Types.TIMESTAMP);
        types.put(Timestamp.class, sql);

        return types;

    }
}
