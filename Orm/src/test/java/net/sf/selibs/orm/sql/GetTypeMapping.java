package net.sf.selibs.orm.sql;

import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;
import net.sf.selibs.orm.types.TypeMapping;
import net.sf.selibs.orm.Connector;
import net.sf.selibs.orm.types.JDBCTypeMapping;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author root
 */
public class GetTypeMapping {

    @Test
    public void getTypeMapping() throws Exception {
        TypeMapping tm = this.getTypeMapping(Connector.getConnection());
        Serializer persister = new Persister();
        StringWriter sw = new StringWriter();
        persister.write(tm, sw);
        System.out.println(sw);
        persister.write(tm, new File("src/main/resources/postgre.xml"));

    }

    public TypeMapping getTypeMapping(Connection con) throws Exception {

        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            String dbName = dbmd.getDatabaseProductName();
            TypeMapping tm = new TypeMapping(dbName);
            rs = dbmd.getTypeInfo();
            while (rs.next()) {
                String typeName = rs.getString("TYPE_NAME");
                short dataType = rs.getShort("DATA_TYPE");
                String createParams = rs.getString("CREATE_PARAMS");
                int nullable = rs.getInt("NULLABLE");
                boolean caseSensitive = rs.getBoolean("CASE_SENSITIVE");
                List<Class> javaTypes = JDBCTypeMapping.getJavaTypes(dataType);
                if (javaTypes != null) {
                    for (Class javaType : javaTypes) {
                        tm.addMapping(javaType.getName(), typeName);
                    }
                }
            }
            return tm;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                }
            }

        }
    }
}
