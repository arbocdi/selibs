package net.sf.selibs.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.selibs.utils.misc.UHelper;

public class DBHelper {

    public static void checkStatus(Connection con, String query) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                rs.getString(1);
            }
        } finally {
            UHelper.close(rs);
            UHelper.close(stmt);
        }
    }

    
}
