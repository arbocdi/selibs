package net.sf.selibs.orm.sql;

import lombok.Data;
import lombok.Setter;

/**
 * Database table.
 *
 * @author selibs
 */
public class SQLTable {
    @Setter
    protected String schema;
    @Setter
    protected String name;

    public SQLTable(String schema, String name) {
        this.schema = schema;
        this.name = name;
    }

    public SQLTable(String name) {
        this.name = name;
    }

    /**
     *
     * @return [schema.]table_name
     */
    public String getTableName() {
        if (this.schema != null&&!this.schema.isEmpty()) {
            return schema + "." + name;
        } else {
            return this.name;
        }
    }

   

}
