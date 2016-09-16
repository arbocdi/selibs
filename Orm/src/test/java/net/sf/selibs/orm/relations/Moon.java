package net.sf.selibs.orm.relations;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Table;
import lombok.ToString;
import net.sf.selibs.orm.JoinField;
import net.sf.selibs.orm.sql.Cascade;

/**
 *
 * @author selibs
 */
@Entity
@Table(name="moon")
@ToString
public class Moon {
    public String name;
    @JoinField(name="planet_id",referencedColumnName="id",referencedClass = Planet.class)
    public int planet_id;
    @JoinColumns({
        @JoinColumn(name="planet_id",referencedColumnName = "id")
    })
    @Cascade
    public Planet planet;
}
