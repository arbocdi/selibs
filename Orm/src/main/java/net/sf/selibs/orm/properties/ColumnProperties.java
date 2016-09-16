package net.sf.selibs.orm.properties;

import java.lang.reflect.Field;

public class ColumnProperties {
    public Field field;
    public Class type;
    public String name;
    public boolean insertable;
    public boolean generatedValue;
    public boolean updatable;
}
