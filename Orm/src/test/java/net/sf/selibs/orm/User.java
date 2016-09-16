package net.sf.selibs.orm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sf.selibs.orm.spec.DatabaseType;

/**
 *
 * @author selibs
 */
@ToString
@Table(name = "users", schema = "data")
public class User {

    public static final String USER_CONST = "Should be excluded form ddl/dml";

    @EmbeddedId
    public UserID userId;
    @Column(name = "name")
    @DatabaseType("text")
    public String username;
    @DatabaseType("text")
    @Setter@Getter
    protected String password;
    @Transient
    public String runtimeId;
    @Column(insertable = false)
    public String nonInsertable;
    @Column(updatable = false)
    public String nonUpdatable;
}
