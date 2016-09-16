package net.sf.selibs.orm.sql;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.JoinColumn;
import net.sf.selibs.orm.JoinField;

/**
 *
 * @author selibs
 */
public class SQLUtils {

    /**
     *
     * @param size number of question marks
     * @return String(?,?,...?)
     */
    public static String makeQuestions(int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 1; i <= size; i++) {
            if (i == size) {
                sb.append("?");
            } else {
                sb.append("?,");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     *
     * @param columns column names
     * @return col1,col2,...,colN
     */
    public static String makeComaSeparated(List<String> columns) {
        StringBuilder sb = new StringBuilder();
        for (String column : columns) {
            sb.append(column);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     *
     * @param columns column names
     * @return col1=?,col2=?,...,colN=?
     */
    public static String makeSetColumns(List<String> columns) {
        StringBuilder sb = new StringBuilder();
        for (String column : columns) {
            sb.append(column);
            sb.append(" = ?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     *
     * @param columns
     * @return col1=? AND col2=?...
     */
    public static String makeAndCondition(List<String> columns) {
        StringBuilder sb = new StringBuilder();
        for (String column : columns) {
            sb.append(column);
            sb.append(" = ? AND ");
        }
        sb.replace(sb.length() - 4, sb.length(), "");
        return sb.toString();
    }

    /**
     *
     * @param commaSeparatedString val1,val2,...
     * @return
     */
    public static List<String> makeListFromComaSeparatedString(String commaSeparatedString) {
        List<String> strings = new LinkedList();
        for (String value : commaSeparatedString.split(",")) {
            strings.add(value);
        }
        return strings;
    }

    /**
     *
     * @param jc
     * @param primaryTable
     * @param referencingTable
     * @return primary.jc.refColName = referencing.jc.name
     */
    public static StringBuilder makeJoinCondition(String referencedColumn, SQLTable primaryTable, String fkColumn,SQLTable referencingTable) {
        StringBuilder sb = new StringBuilder();
        sb.append(primaryTable.getTableName());
        sb.append(".");
        sb.append(referencedColumn);
        sb.append(" = ");
        sb.append(referencingTable.getTableName());
        sb.append(".");
        sb.append(fkColumn);
        return sb;
    }

}
