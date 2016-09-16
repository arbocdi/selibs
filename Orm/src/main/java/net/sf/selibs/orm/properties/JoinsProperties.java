package net.sf.selibs.orm.properties;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.ToString;

@ToString
public class JoinsProperties {

    public List<JoinProperties> joinColumns = new LinkedList();
    public Field field;
    public Class clazz;

    /**
     *
     * @return refColName1 = ? AND refColName2 = ? ...
     */
    public String getSQLCondition() {
        StringBuilder sb = new StringBuilder();
        for (JoinProperties joinColProps : this.joinColumns) {
            sb.append(" ");
            sb.append(joinColProps.refColName);
            sb.append(" = ? AND");
        }
        if (sb.toString().endsWith("AND")) {
            sb.delete(sb.length() - "AND".length(), sb.length());
        }
        return sb.toString();
    }

    public static List<JoinsProperties> getFromClass(Class clazz) {
        List<JoinsProperties> joins = new LinkedList();
        for (Field field : ClassUtils.getAnnotatedFields(clazz, JoinColumns.class)) {
            JoinsProperties join = getFromField(field);
            joins.add(join);
        }
        return joins;
    }

    public static JoinsProperties getFromField(Field field) {
        JoinsProperties joinColsProps = new JoinsProperties();
        joinColsProps.field = field;
        joinColsProps.clazz = field.getType();
        JoinColumns joinCols = field.getAnnotation(JoinColumns.class);
        for (JoinColumn joinCol : joinCols.value()) {
            JoinProperties joinColProps = new JoinProperties();
            joinColProps.name = joinCol.name();
            joinColProps.refColName = joinCol.referencedColumnName();
            joinColsProps.joinColumns.add(joinColProps);
        }
        
        return joinColsProps;
    }
}
