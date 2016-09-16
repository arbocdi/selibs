package net.sf.selibs.orm.sql;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author selibs
 */
@Data
public class SQLGenerator {

    @Getter
    @Setter
    protected SQLTable table;
    @Getter
    @Setter
    protected String tableSynonym = "tbl";

    public SQLGenerator() {
    }

    public SQLGenerator(SQLTable table) {
        this.table = table;
    }

    /**
     * @param condition
     * @return DELETE FROM table_name WHERE condition_for_prepared_statement
     */
    public String getDeleteWhere(String condition) {
        return String.format("DELETE FROM %s\nWHERE %s", table.getTableName(), condition);
    }

    /**
     * @param columns
     * @return INSERT INTO table_name (col1,...colN) VALUES (?,...,?)
     */
    public String getInsert(List<String> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(table.getTableName());
        sb.append(" (");
        sb.append(SQLUtils.makeComaSeparated(columns));
        sb.append(")\nVALUES ");
        sb.append(SQLUtils.makeQuestions(columns.size()));
        return sb.toString();
    }

    /**
     *
     * @param columns all columns
     * @param pkColumns primary key columns
     * @return INSERT INTO table_name (col1,...colN) VALUES (?,...,?) RETURNING
     * pkCol1,pkCol2...
     */
    public String getInsertReturning(List<String> columns, List<String> pkColumns) {
        String sql = this.getInsert(columns);
        sql = sql + " RETURNING " + SQLUtils.makeComaSeparated(pkColumns);
        return sql;
    }

    /**
     *
     * @param where
     * @param set
     * @return UPDATE table_name SET _set_ WHERE _where_
     */
    public String getUpdateWhereSet(String where, String set) {
        return String.format("UPDATE %s\nSET %s\nWHERE %s", table.getTableName(), set, where);
    }

    /**
     *
     * @param pkProperties
     * @param set
     * @return UPDATE table_name SET _set_ WHERE pkCol1=? AND pkCol2=?...
     */
    public String getUpdatePKSet(List<String> columns, String set) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(this.table.getTableName());
        sb.append("\nSET ");
        sb.append(set);
        sb.append("\nWHERE ");
        sb.append(SQLUtils.makeAndCondition(columns));
        return sb.toString();
    }

    /**
     *
     * @return DROP TABLE table_name
     */
    public String getDropTable() {
        return String.format("DROP TABLE %s CASCADE", this.table.getTableName());
    }

    /**
     *
     * @return DELETE FROM table_name
     */
    public String getClearTable() {
        return String.format("DELETE FROM %s", this.table.getTableName());
    }

    /**
     *
     * @param columns
     * @return SELECT col1,col2... FROM table
     */
    public String getSelectFromTable(List<String> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(SQLUtils.makeComaSeparated(columns));
        sb.append("\nFROM ");
        sb.append(this.table.getTableName());
        sb.append(" ");
        sb.append(this.getTableSynonym());
        return sb.toString();
    }
        /**
     *
     * @param columns
     * @param condition
     * @return SELECT col1,col2... FROM table WHERE condition
     */
    public String getSelectWithCondition(List<String> columns, String condition) {
        StringBuilder sb = new StringBuilder();
        sb.append(getSelectFromTable(columns));
        sb.append("\nWHERE ");
        sb.append(condition);
        return sb.toString();
    }
    /**
     * add table name to column names
     * @param columns
     * @return 
     */
    public List<String> appendTableName(List<String> columns){
        List<String> fullNames = new LinkedList();
        for(String column:columns){
            fullNames.add(this.table.getTableName()+"."+column);
        }
        return fullNames;
    }
    /**
     * 
     * @param columns
     * @return DELETE FROM table WHERE pk1 = ? AND pk2 = ? ...
     */
    public String getRemove(List<String> columns){
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(this.table.getTableName());
        sb.append("\nWHERE ");
        sb.append(SQLUtils.makeAndCondition(columns));
        return sb.toString();
    }
}
