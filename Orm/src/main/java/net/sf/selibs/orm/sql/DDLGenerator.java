package net.sf.selibs.orm.sql;

import net.sf.selibs.orm.types.TypeConverter;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import net.sf.selibs.orm.ParentDAO;
import net.sf.selibs.orm.properties.ClassUtils;
import net.sf.selibs.orm.properties.EntityProperties;
import net.sf.selibs.orm.properties.JoinProperties;
import net.sf.selibs.orm.properties.JoinsProperties;
import net.sf.selibs.orm.properties.PKProperties;

public class DDLGenerator {

    public static final String CREATE_TABLE = "CREATE TABLE #tableName# \n(\n#definition#\n)";
    public static final String COLUMN_DEF = "#colName# #colType# #unique# #nullable# #colDef#";
    @Setter
    @Getter
    protected TypeConverter typeConverter;

    /**
     *
     * @param ep
     * @param daos
     * @return CREATE TABLE name 
     * (
     * col1,col2,... 
     * PRIMARY KEY (...), 
     * FOREIGN KEY (...)
     * )
     */
    public String makeCreateTable(EntityProperties ep, Map<Class, ParentDAO> daos) {
        StringBuilder sb = new StringBuilder();
        //append header
        sb.append("CREATE TABLE ");
        sb.append(ep.getSqlGenerator().getTable().getTableName());
        sb.append(" (\n");
        //append columns
        PKProperties pkProps = ep.getPkProperties();
        List<Field> pkFields = new LinkedList();
        if (pkProps != null) {
            pkFields = pkProps.getTableColumns().readableFields;
        }
        for (Field field : ep.getAllReadableFields()) {
            sb.append(this.makeColumnDefinition(field, pkFields));
            sb.append(",\n");
        }
        //appendPK
        if (pkProps != null) {
            sb.append(this.makePrimaryKey(pkFields));
            sb.append(",\n");
        }
        //appendFKs
        for (JoinsProperties joinsProps : ep.getJoinColumnsProperties()) {
            String referencedTable = daos.get(joinsProps.clazz).getEntityProperties().getSqlGenerator().getTable().getTableName();
            sb.append(makeForeignKey(joinsProps, referencedTable));
            sb.append(",\n");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("\n)");
        return sb.toString();
    }

    /**
     *
     * @param field
     * @param typeConverter
     * @param pkFields
     * @return colName [colType [UNIQUE] [NOT NULL]] | [COL_DEF]
     */
    protected String makeColumnDefinition(Field field, List<Field> pkFields) {
        Column column = field.getAnnotation(Column.class);
        String columnName = field.getName();
        String unique = "";
        String nullable = "";
        String colDef = "";
        String colType = "";
        if (column != null) {
            colDef = column.columnDefinition();
            if (colDef.isEmpty()) {
                columnName = column.name().isEmpty() ? columnName : column.name();
                unique = column.unique() ? "UNIQUE" : "";
                nullable = column.nullable() ? "" : "NOT NULL";
                colType = typeConverter.getType(field.getType());
                if (pkFields.contains(field)) {
                    nullable = "NOT NULL";
                }
            }

        } else {
            colType = typeConverter.getType(field.getType());
            if (pkFields.contains(field)) {
                nullable = "NOT NULL";
            }
        }

        String sql = COLUMN_DEF.replace("#colName#", columnName).
                replace("#unique#", unique).
                replace("#nullable#", nullable).
                replace("#colDef#", colDef).
                replace("#colType#", colType);
        return sql;
    }

    /**
     *
     * @param pkFields
     * @return PRIMARY KEY (col1,col2,...)
     */
    protected String makePrimaryKey(List<Field> pkFields) {
        StringBuilder sb = new StringBuilder();
        sb.append("PRIMARY KEY (");
        for (Field pkField : pkFields) {
            sb.append(ClassUtils.getColumnName(pkField));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    /**
     *
     * @return FOREIGN KEY (col1,col2,...) REFERENCES table(col1,col2,...)
     * CASCADE_TYPE,[UNIQUE(col1,col2)]
     */
    protected String makeForeignKey(JoinsProperties jp, String referencedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("FOREIGN KEY (");
        for (JoinProperties joinProps : jp.joinColumns) {
            sb.append(joinProps.name);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") REFERENCES ");
        sb.append(referencedTable);
        sb.append("(");
        for (JoinProperties joinProps : jp.joinColumns) {
            sb.append(joinProps.refColName);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Cascade cascade = jp.field.getAnnotation(Cascade.class);
        if (cascade != null) {
            sb.append(cascade.action());
        }
        OneToOne one2one = jp.field.getAnnotation(OneToOne.class);
        if (one2one != null) {
            sb.append(",\n");
            sb.append("UNIQUE (");
            for (JoinProperties joinProps : jp.joinColumns) {
                sb.append(joinProps.name);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);

            sb.append(")");
        }
        return sb.toString();
    }

}
