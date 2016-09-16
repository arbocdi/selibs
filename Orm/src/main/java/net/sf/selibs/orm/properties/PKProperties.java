package net.sf.selibs.orm.properties;

import java.util.List;
import lombok.Getter;

/**
 *
 * @author selibs
 */
public class PKProperties {

    @Getter
    protected final TableColumns tableColumns;
    @Getter
    protected final Class pkClass;

    public PKProperties(Class pkClass) {
        this.tableColumns = ClassUtils.getTableColumns(pkClass);
        this.pkClass = pkClass;
    }

    public List<String> getFieldNames() {
        return this.tableColumns.readableNames;
    }
}
