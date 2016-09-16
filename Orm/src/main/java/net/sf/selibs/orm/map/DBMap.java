package net.sf.selibs.orm.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.selibs.orm.JDBCUtils;
import net.sf.selibs.utils.misc.UHelper;

public class DBMap {

    public static Object executeStatement(Connection con, String sql, QueryType queryType, Object... values) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(sql);
            if (values != null) {
                JDBCUtils.setToPreparedStatement(stmt, values);
            }
            switch (queryType) {
                case UPDATE:
                    return stmt.executeUpdate();
                case QUERY:
                    rs = stmt.executeQuery();
                    return getFromRS(rs);
                default:
                    throw new SQLException(String.format("Unexpected QueryType %s", queryType));
            }
        } finally {
            UHelper.close(rs);
            UHelper.close(stmt);
        }
    }

    public static Set<String> getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Set<String> columnNames = new HashSet();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            columnNames.add(meta.getColumnLabel(i));
        }
        return columnNames;
    }

    public static Record getSingleRecord(ResultSet rs, Set<String> columnNames) throws SQLException {
        Record record = new Record();
        for (String cn : columnNames) {
            record.add(cn, rs.getObject(cn));
        }
        return record;

    }

    public static List<Record> getFromRS(ResultSet rs) throws SQLException {
        Set<String> columnNames = getColumnNames(rs);
        List<Record> records = new ArrayList();
        while (rs.next()) {
            records.add(getSingleRecord(rs, columnNames));
        }
        return records;
    }

}
