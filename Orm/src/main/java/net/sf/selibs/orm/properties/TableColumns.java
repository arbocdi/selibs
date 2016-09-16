package net.sf.selibs.orm.properties;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class TableColumns {

    //insertable columns

    public final List<ColumnProperties> insertable = new LinkedList();
    public final List<String> insertableNames = new LinkedList();
    public final List<Field> insertableFields = new LinkedList();
    //updatable columns
    public final List<ColumnProperties> updatable = new LinkedList();
    public final List<String> updatableNames = new LinkedList();
    public final List<Field> updatableFields = new LinkedList();
    //readable columns
    public final List<ColumnProperties> readable = new LinkedList();
    public final List<String> readableNames = new LinkedList();
    public final List<Field> readableFields = new LinkedList();

    public ColumnProperties getColumnProperties(String dbColumnName) {
        for (ColumnProperties column : readable) {
            if (column.name.equals(dbColumnName)) {
                return column;
            }
        }
        return null;
    }
}
